package com.codedthoughts.codedthoughts.views;

import jakarta.annotation.Nullable;
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
    private UUID blogId;
    @NotNull private String blogTitle;
    @NotNull private String username;
    private String blogSubTitle;
    private String contents;
    private Date created_on;
    private Long likes;
}
