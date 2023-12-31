package com.codedthoughts.codedthoughts.controllers;

import com.codedthoughts.codedthoughts.entities.Blog;
import com.codedthoughts.codedthoughts.entities.BlogAttachment;
import com.codedthoughts.codedthoughts.exceptions.NoSuchElementPresentException;
import com.codedthoughts.codedthoughts.services.BlogService;
import com.codedthoughts.codedthoughts.views.BlogView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
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
            blogService.fetchAllBlogs().forEach(blog -> {
                blogs.add(BlogView
                        .builder()
                        .blogId(blog.getUniqueId())
                        .blogTitle(blog.getTitle())
                        .blogSubTitle(blog.getSub_title())
                        .contents(blog.getContents())
                        .username(blog.getUser().getUsername())
                        .likes(blog.getLikes())
                        .created_on(blog.getCreatedOn())
                        .build());
            });
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
                    .username(blog.getUser().getUsername())
                    .likes(blog.getLikes())
                    .created_on(blog.getCreatedOn())
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

    @PostMapping("/add-att/{draftedBlogId}")
    public ResponseEntity<?> addBlogAtt(@PathVariable("draftedBlogId") UUID draftedBlogId, @RequestBody MultipartFile file) {
        if(!file.isEmpty()) {
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            try {
                UUID blogAttId = blogService.addBlogAttachment(file, draftedBlogId);
                return ResponseEntity.ok(Map.of(
                        "attUrl", baseUrl + "/api/blog/get-att/" + blogAttId
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

    @GetMapping("/get-att/{blogAttId}")
    public ResponseEntity<?> getBlogAtt(@PathVariable("blogAttId") UUID blogAttId) {
        try{
            BlogAttachment blogAtt = blogService.getBlogAttachment(blogAttId);
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
}
