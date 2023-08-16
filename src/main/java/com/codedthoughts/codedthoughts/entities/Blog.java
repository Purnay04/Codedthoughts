package com.codedthoughts.codedthoughts.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

//    @ManyToMany(fetch = FetchType.LAZY)
//    private Set<User> bookmarkBy = new HashSet<>();

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
