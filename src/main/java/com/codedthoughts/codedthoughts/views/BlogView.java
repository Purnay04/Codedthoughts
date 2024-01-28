package com.codedthoughts.codedthoughts.views;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class BlogView {
    @NotNull private UUID blogId;
    @NotNull private String blogTitle;
    private String blogSubTitle;
    private String contents;
    private Date created_on;
    private Long likes;
    private Boolean likedByUser;
    private AuthorDetails authorDetails;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class AuthorDetails {
        private String username;
    }
}
