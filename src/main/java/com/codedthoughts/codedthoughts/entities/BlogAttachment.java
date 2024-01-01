package com.codedthoughts.codedthoughts.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.util.Objects;
import java.util.UUID;

@Builder
@Entity
@Table(name = "ctblogatt")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogAttachment extends BaseEntity{
    @Id
    @TableGenerator(
            name = "CT_SEQ",
            table = "ctseq",
            pkColumnName = "seq_name",
            valueColumnName = "seq_value",
            pkColumnValue = "ct_blogatt_pk",
            allocationSize = 4
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CT_SEQ")
    @Column(name = "id")
    private Integer blogAttId;

    @Column(name = "unique_id")
    private UUID uniqueId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_contents")
    private Blob fileContents;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "checksum")
    private String checksum;

    @Column(name = "blog_unique_id")
    private UUID blogId;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @PrePersist
    public void prePersist() {
        this.setUniqueId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlogAttachment blogAttachment)) return false;
        return Objects.equals(blogAttId, blogAttachment.blogAttId);
    }

    @Override
    public int hashCode() { return Objects.hash(blogAttId); }
}
