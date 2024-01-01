package com.codedthoughts.codedthoughts.repo;

import com.codedthoughts.codedthoughts.entities.BlogAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BlogAttachmentRepository extends JpaRepository<BlogAttachment, Integer> {
    public Set<BlogAttachment> findByBlogId(UUID blogId);
    public Optional<BlogAttachment> findByUniqueId(UUID attachmentId);
}
