package com.codedthoughts.codedthoughts.controllers;

import com.codedthoughts.codedthoughts.entities.Blog;
import com.codedthoughts.codedthoughts.entities.BlogAttachment;
import com.codedthoughts.codedthoughts.exceptions.NoSuchElementPresentException;
import com.codedthoughts.codedthoughts.exceptions.UserActionInvalidException;
import com.codedthoughts.codedthoughts.services.BlogService;
import com.codedthoughts.codedthoughts.views.BlogView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogApi {

    private static final Logger logger = LoggerFactory.getLogger(BlogApi.class);
    private final BlogService blogService;

    @PostMapping("/save")
    public ResponseEntity<?> addBlog(@RequestBody @Valid BlogView blogView) {
        try {
            this.blogService.saveBlog(blogView);
        } catch (Exception e) {
            logger.debug(String.format("Exception at /blog/add api: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard-blogs")
    public ResponseEntity<?> getBlogs() {
        List<BlogView> blogs = new ArrayList<>();
        try {
            blogService.fetchAllBlogs().forEach(blog -> blogs.add(BlogView
                    .builder()
                    .blogId(blog.getUniqueId())
                    .blogTitle(blog.getTitle())
                    .blogSubTitle(blog.getSub_title())
                    .contents(blog.getContents())
                    .likes(blog.getLikes())
                    .created_on(blog.getCreatedOn())
                    .authorDetails(new BlogView.AuthorDetails(blog.getUser().getUsername()))
                    .build()));
        } catch (Exception e) {
            logger.debug(String.format("Exception at /blog/dashboard-blogs api: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(Map.of("blogs", blogs));
    }

    @GetMapping("/view/{blogId}")
    public ResponseEntity<?> getBlog(@PathVariable(name = "blogId") UUID blogId) {
        BlogView blogView;
        try {
            Blog blog = blogService.fetchBlog(blogId);
            blogView = BlogView
                    .builder()
                    .blogId(blog.getUniqueId())
                    .blogTitle(blog.getTitle())
                    .blogSubTitle(blog.getSub_title())
                    .contents(blog.getContents())
                    .likes(blog.getLikes())
                    .created_on(blog.getCreatedOn())
                    .authorDetails(new BlogView.AuthorDetails(blog.getUser().getUsername()))
                    .build();
        } catch (NoSuchElementPresentException nosep) {
            logger.debug(String.format("NoSuchElementPresentException at /blog/view api: %s", nosep.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(nosep.getMessage());
        } catch (Exception e) {
            logger.debug(String.format("Exception at /blog/view api: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(Map.of("blog", blogView));
    }

    @PostMapping("/addAttachment/{draftedBlogId}")
    public ResponseEntity<?> addBlogAtt(@PathVariable("draftedBlogId") UUID draftedBlogId, @RequestBody MultipartFile file) {
        if(!file.isEmpty()) {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            try {
                UUID blogAttId = blogService.addBlogAttachment(file, draftedBlogId);
                return ResponseEntity.ok(Map.of(
                        "attUrl", baseUrl + "/api/blog/attachment/" + blogAttId
                ));
            } catch (InvalidMimeTypeException imte) {
                logger.debug("File Error at /blog/add-att: Invalid file attached!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(imte.getMessage());
            } catch (Exception e) {
                logger.debug(String.format("Exception at /blog/add-att api: %s", e.getMessage()));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            logger.debug("File Error at /blog/add-att: Invalid file attached!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/attachment/{attachmentId}")
    public ResponseEntity<?> getBlogAtt(@PathVariable("attachmentId") UUID attachmentId) {
        try{
            BlogAttachment blogAtt = blogService.getBlogAttachment(attachmentId);
            Optional<byte[]> contents = blogService.blobToByteArray(blogAtt.getFileContents());
            HttpHeaders headers = new HttpHeaders();
            if(contents.isPresent()) {
                headers.setContentType(MediaType.parseMediaType(blogAtt.getContentType()));
                headers.setContentLength(contents.get().length);
            }
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(contents.orElseGet(() -> new byte[]{}));
        } catch (IOException | SQLException e) {
            logger.debug("File Retrieve error at /blog/get-att");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/referAttachment/{attachmentId}")
    public ResponseEntity<?> saveReferredAttachmentInstance(@PathVariable("attachmentId") UUID attachmentId) {
        try {
            this.blogService.addRefToAttachment(attachmentId);
        } catch (NoSuchElementPresentException nsep) {
            logger.debug(String.format("Invalid Attachment Id Provided: %s", attachmentId));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/removeAttachment/{attachmentId}")
    public ResponseEntity<?> removeAttachment(@PathVariable("attachmentId") UUID attachmentId) {
        try {
            this.blogService.removeAttachment(attachmentId);
        } catch (NoSuchElementPresentException nsep) {
            logger.debug(String.format("Invalid Attachment Id Provided: %s", attachmentId));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("{blogId}/action")
    public ResponseEntity<?> addUserLike(@PathVariable("blogId") UUID blogId, @RequestParam("action") String action) {
        Map<String, Object> actionResponse;
        try {
            actionResponse = this.blogService.handleUserAction(blogId, action);
        } catch(UsernameNotFoundException unfe) {
            logger.debug(unfe.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unfe.getMessage());
        } catch ( UserActionInvalidException uaie) {
            logger.debug(uaie.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(uaie.getMessage());
        } catch (NoSuchElementPresentException | IllegalArgumentException e) {
            logger.debug(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().body(actionResponse);
    }
}
