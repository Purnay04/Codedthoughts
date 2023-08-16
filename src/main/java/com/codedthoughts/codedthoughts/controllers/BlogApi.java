package com.codedthoughts.codedthoughts.controllers;

import com.codedthoughts.codedthoughts.entities.Blog;
import com.codedthoughts.codedthoughts.exceptions.NoSuchElementPresentException;
import com.codedthoughts.codedthoughts.services.BlogService;
import com.codedthoughts.codedthoughts.views.BlogView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogApi {

    private static final Logger logger = LoggerFactory.getLogger(BlogApi.class);
    private final BlogService blogService;

    @PostMapping("/add")
    public ResponseEntity<?> addBlog(@RequestBody @Valid BlogView blogView) {
        try {
            blogService.createBlog(blogView);
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
                        .blogId(blog.getBlogId())
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
    public ResponseEntity<?> getBlog(@PathVariable(name = "blogId") Integer blogId) {
        BlogView blogView;
        try {
            Blog blog = blogService.fetchBlog(blogId);
            blogView = BlogView
                    .builder()
                    .blogId(blog.getBlogId())
                    .blogTitle(blog.getTitle())
                    .blogSubTitle(blog.getSub_title())
                    .contents(blog.getContents())
                    .username(blog.getUser().getUsername())
                    .likes(blog.getLikes())
                    .created_on(blog.getCreatedOn())
                    .build();
        } catch (NoSuchElementPresentException nosep) {
            logger.debug(String.format("NoSuchElementPresentException at /blog/view api: %s", nosep.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(nosep.getMessage());
        } catch (Exception e) {
            logger.debug(String.format("Exception at /blog/view api: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(Map.of("blog", blogView));
    }
}
