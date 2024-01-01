package com.codedthoughts.codedthoughts.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "ctblog")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Blog extends BaseEntity{

    @Id
    @TableGenerator(
            name = "CT_SEQ",
            table = "ctseq",
            pkColumnName = "seq_name",
            valueColumnName = "seq_value",
            pkColumnValue = "ct_blog_pk",
            allocationSize = 4
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CT_SEQ")
    @Column(name = "id")
    private Integer blogId;

    @Column(name = "unique_id")
    private UUID uniqueId;

    @Column(name = "title")
    private String title;

    @Column(name = "sub_title")
    private String sub_title;

    @Column(name = "likes")
    private long likes;

    @Column(name = "isPrivate", columnDefinition = "TINYINT(1)")
    private boolean isPrivate;

    @Lob
    @Column(name = "contents", columnDefinition = "LONGTEXT")
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BlogAttachment> inlineAttachments;

//    @ManyToMany(fetch = FetchType.LAZY)
//    private Set<User> bookmarkBy = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if(!CollectionUtils.isEmpty(inlineAttachments)) {
            inlineAttachments.forEach(att -> {
                att.setBlogId(this.getUniqueId());
                att.setBlog(this);
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Blog blog)) return false;
        return Objects.equals(blogId, blog.blogId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blogId);
    }
}
