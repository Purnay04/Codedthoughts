package com.codedthoughts.codedthoughts.repo;

import com.codedthoughts.codedthoughts.entities.Blog;
import com.codedthoughts.codedthoughts.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    public Optional<Blog> findByTitle(String title);
    public Optional<Blog> findByBlogId(Integer blogId);
}
