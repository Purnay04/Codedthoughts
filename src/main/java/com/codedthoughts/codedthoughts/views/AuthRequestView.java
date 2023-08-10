package com.codedthoughts.codedthoughts.views;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
public class AuthRequestView implements Serializable {
    @NotNull
    private String userName;

    @NotNull
    private String password;
}
