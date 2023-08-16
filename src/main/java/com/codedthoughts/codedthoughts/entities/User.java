package com.codedthoughts.codedthoughts.entities;

import com.codedthoughts.codedthoughts.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ctuser")
public class User extends BaseEntity implements UserDetails {
    @Id
    @TableGenerator(
            name = "CT_SEQ",
            table = "ctseq",
            pkColumnName = "seq_name",
            valueColumnName = "seq_value",
            pkColumnValue = "ct_user_pk",
            allocationSize = 4
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "CT_SEQ")
    @Column(name = "id")
    private Integer userId;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date DOB;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Blog> blogs = new HashSet<>();

//    @ManyToMany(mappedBy = "bookmarkBy", fetch = FetchType.EAGER)
//    private Set<Blog> bookmarks = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }

}
