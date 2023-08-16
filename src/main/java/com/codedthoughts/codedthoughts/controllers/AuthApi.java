package com.codedthoughts.codedthoughts.controllers;

import com.codedthoughts.codedthoughts.exceptions.UserAlreadyExistException;
import com.codedthoughts.codedthoughts.services.AuthService;
import com.codedthoughts.codedthoughts.views.AuthRequestView;
import com.codedthoughts.codedthoughts.views.SignupRequestView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApi {
    private static final Logger logger = LoggerFactory.getLogger(AuthApi.class);
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequestView authRequestView){
        try {
            logger.debug(String.format("in login Api for user: %s", authRequestView.getUsername()));
            return ResponseEntity.ok(authService.doLogin(authRequestView).orElseThrow());
        } catch (BadCredentialsException bce){
            logger.debug(String.format("BadCredentialsException at login api: %s", bce.getMessage()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("errorMsg", bce.getMessage()));
        } catch (Exception e) {
            logger.debug(String.format("Exception at login api: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignupRequestView signupView) {
        try {
            logger.debug(String.format("in signup Api for: %s", signupView.toString()));
            return ResponseEntity.ok(authService.doSignup(signupView).orElseThrow());
        } catch (UserAlreadyExistException uae) {
            logger.debug(String.format("UserAlreadyExistException at signup api: %s", uae.getMessage()));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(uae.getMsg());
        } catch (BadCredentialsException bce) {
            logger.debug(String.format("BadCredentialsException at signup api: %s", bce.getMessage()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("errorMsg", bce.getMessage()));
        } catch (Exception e) {
            logger.debug(String.format("Exception at signup api: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
