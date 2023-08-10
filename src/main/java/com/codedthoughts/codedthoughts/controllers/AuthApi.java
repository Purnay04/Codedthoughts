package com.codedthoughts.codedthoughts.controllers;

import com.codedthoughts.codedthoughts.exceptions.UserAlreadyExistException;
import com.codedthoughts.codedthoughts.services.AuthService;
import com.codedthoughts.codedthoughts.views.AuthRequestView;
import com.codedthoughts.codedthoughts.views.LoginResponseView;
import com.codedthoughts.codedthoughts.views.SignupRequestView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApi {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseView> login(@RequestBody @Valid AuthRequestView authRequestView){
        try {
            return ResponseEntity.ok(authService.doLogin(authRequestView).orElseThrow());
        } catch (BadCredentialsException bce){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignupRequestView signupView) {
        try {
            authService.doSignup(signupView);
            return ResponseEntity.ok(Map.of());
        } catch (UserAlreadyExistException uae) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(uae.getMsg());
        }
    }
}
