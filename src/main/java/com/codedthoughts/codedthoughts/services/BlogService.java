package com.codedthoughts.codedthoughts.services;

import com.codedthoughts.codedthoughts.entities.Blog;
import com.codedthoughts.codedthoughts.exceptions.NoSuchElementPresentException;
import com.codedthoughts.codedthoughts.repo.BlogRepository;
import com.codedthoughts.codedthoughts.views.BlogView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserService userService;

    @Transactional
    public void createBlog(BlogView blogView) {
        Blog newBlog = Blog
                .builder()
                .title(blogView.getBlogTitle())
                .sub_title(blogView.getBlogSubTitle())
                .contents(blogView.getContents())
                .user(userService.getUserByUserName(blogView.getUsername()))
                .isPrivate(false)
                .likes(0)
                .build();
        blogRepository.save(newBlog);
    }

    @Transactional(readOnly = true)
    public List<Blog> fetchAllBlogs() {
        return blogRepository.findAll();
    }

    @Transactional(readOnly = true, rollbackFor = NoSuchElementPresentException.class)
    public Blog fetchBlog(Integer blogId) throws NoSuchElementPresentException {
        return blogRepository.findByBlogId(blogId).orElseThrow(() -> new NoSuchElementPresentException(Integer.toString(blogId)));
    }
}
