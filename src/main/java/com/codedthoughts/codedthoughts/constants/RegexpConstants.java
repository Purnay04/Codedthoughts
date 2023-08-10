package com.codedthoughts.codedthoughts.constants;

public class RegexpConstants {
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]+$";
    public static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
}
