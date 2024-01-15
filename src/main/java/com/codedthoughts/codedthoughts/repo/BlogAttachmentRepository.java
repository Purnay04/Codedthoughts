package com.codedthoughts.codedthoughts.repo;

import com.codedthoughts.codedthoughts.entities.BlogAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface BlogAttachmentRepository extends JpaRepository<BlogAttachment, Integer> {
    public Set<BlogAttachment> findByBlogId(UUID blogId);
    public Optional<BlogAttachment> findByUniqueId(UUID attachmentId);

    @Query("SELECT blogAtt from BlogAttachment blogAtt where blogAtt.checksum = :checksum and blogAtt.blogId = :blogId")
    public Optional<BlogAttachment> findByChecksumAndBlogId(String checksum, UUID blogId);
}
