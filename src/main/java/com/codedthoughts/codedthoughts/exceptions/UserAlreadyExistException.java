package com.codedthoughts.codedthoughts.exceptions;

import lombok.Getter;

@Getter
public class UserAlreadyExistException extends Throwable {
    final String msg;
    public UserAlreadyExistException(String token) {
        msg = String.format("%s Already in use!!%n", token);
    }
}
