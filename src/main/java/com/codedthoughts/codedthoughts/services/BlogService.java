package com.codedthoughts.codedthoughts.services;

import com.codedthoughts.codedthoughts.entities.Blog;
import com.codedthoughts.codedthoughts.entities.BlogAttachment;
import com.codedthoughts.codedthoughts.exceptions.NoSuchElementPresentException;
import com.codedthoughts.codedthoughts.repo.BlogAttachmentRepository;
import com.codedthoughts.codedthoughts.repo.BlogRepository;
import com.codedthoughts.codedthoughts.views.BlogView;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {
    private static final Logger logger = LoggerFactory.getLogger(BlogService.class);

    private final BlogRepository blogRepository;
    private final UserService userService;
    private final BlogAttachmentRepository blogAttRepository;
    private final SystemPropertiesService sysPropService;

    public Blob convertByteArrayToBlob(byte[] byteArray) {
        try {
            return new SerialBlob(byteArray);
        } catch (Exception e) {
            return null;
        }
    }

    private String calculateMD5Checksum(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] checksumBytes = md.digest(content);
        return Hex.encodeHexString(checksumBytes);
    }

    public Optional<byte[]> blobToByteArray(Blob blob) throws IOException, SQLException {
        if(ObjectUtils.isEmpty(blob)) {
            return Optional.of(new byte[]{});
        }
        try(InputStream inputStream = blob.getBinaryStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return Optional.of(outputStream.toByteArray());
        } catch (IOException | SQLException e) {
            logger.debug(String.format("Error while converting blob to byte array!!: \n %s", e.getMessage()));
            throw e;
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<String> getSupportedMimeTypes() throws NoSuchElementPresentException{
        String mimeTypes = (String) sysPropService.getSysPropertyByName(SystemPropertiesService.SUPPORTING_MIME_TYPES);
        return Arrays
                .stream(mimeTypes.split(","))
                .distinct()
                .collect(Collectors.toList());
    }

    public void removeAttachments(Set<BlogAttachment> attachments) {
        attachments.forEach(this.blogAttRepository::delete);
    }

    public Set<BlogAttachment> fetchAllInlineAttachmentFromContent(String htmlContent, UUID blogId) {
        return fetchAllInlineAttachmentFromContent(htmlContent, blogId, null);
    }

    @Transactional(readOnly = true)
    public Set<BlogAttachment> fetchAllInlineAttachmentFromContent(String htmlContent, UUID blogId, Blog blog) {
        Document document = Jsoup.parse(htmlContent);

        List<String> inlineAttachmentIds = new ArrayList<>();
        Elements imageTags = document.select("img");
        for(Element imgElement: imageTags) {
            String imgUrl = imgElement.attr("src");
            if(imgUrl.startsWith("http://localhost:8080/api/blog")) {
                String[] uriComponent = imgUrl.split("/");
                inlineAttachmentIds.add(uriComponent[uriComponent.length - 1]);
            }
        }

        if(!inlineAttachmentIds.isEmpty()) {
            Set<BlogAttachment> inlineAttachments = inlineAttachmentIds.stream()
                    .map(uuid -> {
                        BlogAttachment blogAttachment = this.blogAttRepository.findByUniqueId(UUID.fromString(uuid)).orElseThrow();
                        if(!ObjectUtils.isEmpty(blog) && !blog.getInlineAttachments().contains(blogAttachment)) {
                            blogAttachment.setBlog(blog);
                        }
                        return blogAttachment;
                    })
                    .collect(Collectors.toSet());

            //remove if any non-linked attachments is left
            Set<BlogAttachment> allAttachmentsAsPerBlogId = this.blogAttRepository.findByBlogId(blogId);
            removeAttachments(allAttachmentsAsPerBlogId.stream().filter(att -> !inlineAttachments.contains(att)).collect(Collectors.toSet()));

            return inlineAttachments;
        }
        return Set.of();
    }

    public Blog checkBlogExist(UUID blogId) {
        return this.blogRepository.findByUniqueIdWithInlineAttachments(blogId).orElse(null);
    }

    @Transactional
    public void createBlog(BlogView blogView) {
        Set<BlogAttachment> blogAttachments = fetchAllInlineAttachmentFromContent(blogView.getContents(), blogView.getBlogId());
        Blog newBlog = Blog
                .builder()
                .uniqueId(blogView.getBlogId())
                .title(blogView.getBlogTitle())
                .sub_title(blogView.getBlogSubTitle())
                .contents(blogView.getContents())
                .inlineAttachments(blogAttachments)
                .user(userService.getUserByUserName(blogView.getUsername()))
                .isPrivate(false)
                .likes(0)
                .build();
        blogRepository.save(newBlog);
    }

    public void updateBlog(BlogView blogView, Blog blog) {
        Set<BlogAttachment> blogAttachments = fetchAllInlineAttachmentFromContent(blogView.getContents(), blogView.getBlogId(), blog);
        //compare title to identify change
        if(!blog.getTitle().equalsIgnoreCase(blogView.getBlogTitle())) {
            blog.setTitle(blogView.getBlogTitle());
        }
        //compare subtitle to identify change
        if(!blog.getSub_title().equalsIgnoreCase(blogView.getBlogSubTitle())) {
            blog.setSub_title(blogView.getBlogSubTitle());
        }
        //compare contents to identify change
        if(!blog.getContents().equalsIgnoreCase(blogView.getContents())) {
            blog.setContents(blogView.getContents());
        }

        blog.getInlineAttachments().clear();
        blog.getInlineAttachments().addAll(blogAttachments);
        this.blogRepository.save(blog);
    }

    public void saveBlog(BlogView blogView) {
        Blog blog = this.checkBlogExist(blogView.getBlogId());
        if(ObjectUtils.isEmpty(blog)) {
            this.createBlog(blogView);
            return;
        }
        this.updateBlog(blogView, blog);
    }

    @Transactional(readOnly = true)
    public List<Blog> fetchAllBlogs() {
        return blogRepository.findAll();
    }

    @Transactional(readOnly = true, rollbackFor = NoSuchElementPresentException.class)
    public Blog fetchBlog(UUID blogId) throws NoSuchElementPresentException {
        return blogRepository.findByUniqueId(blogId).orElseThrow(() -> new NoSuchElementPresentException(blogId.toString()));
    }

    @Transactional(rollbackFor = {IOException.class, NoSuchAlgorithmException.class})
    public UUID addBlogAttachment(MultipartFile file, UUID draftedBlogId) throws IOException, NoSuchAlgorithmException, InvalidMimeTypeException {
        List<String> supportedMimeTypes = getSupportedMimeTypes();
        if(!ObjectUtils.isEmpty(file.getContentType()) && supportedMimeTypes.contains(file.getContentType())) {
            String checksum = calculateMD5Checksum(file.getBytes());
            BlogAttachment blogAtt = BlogAttachment
                    .builder()
                    .fileName(file.getName())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .fileContents(convertByteArrayToBlob(file.getBytes()))
                    .blogId(draftedBlogId)
                    .checksum(checksum)
                    .build();
            blogAttRepository.save(blogAtt);
            return blogAtt.getUniqueId();
        }
        logger.debug(String.format("Invalid file type attached: %s", file.getContentType()));
        throw new InvalidMimeTypeException(file.getContentType(), "Invalid file Attached!!");
    }

    @Transactional(readOnly = true, rollbackFor = NoSuchElementPresentException.class)
    public BlogAttachment getBlogAttachment(UUID attId) throws NoSuchElementPresentException {
        return blogAttRepository.findByUniqueId(attId).orElseThrow(() -> new NoSuchElementPresentException(String.valueOf(attId)));
    }
}
