package com.codedthoughts.codedthoughts.repo;

import com.codedthoughts.codedthoughts.entities.Blog;
import com.codedthoughts.codedthoughts.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    public Optional<Blog> findByTitle(String title);
    public Optional<Blog> findByUniqueId(UUID blogId);
    @Query("SELECT blog from Blog blog JOIN FETCH blog.inlineAttachments where blog.uniqueId = :blogId")
    public Optional<Blog> findByUniqueIdWithInlineAttachments(UUID blogId);
}
