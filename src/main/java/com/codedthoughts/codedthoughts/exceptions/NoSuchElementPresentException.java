package com.codedthoughts.codedthoughts.exceptions;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class NoSuchElementPresentException extends RuntimeException {
    final String msg;
    public NoSuchElementPresentException(String token) {
        super("No such %s element present".formatted(token));
        msg = String.format("No such %s element present", token);
    }
}
