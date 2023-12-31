package com.codedthoughts.codedthoughts.services;

import com.codedthoughts.codedthoughts.entities.User;
import com.codedthoughts.codedthoughts.enums.Role;
import com.codedthoughts.codedthoughts.exceptions.UserAlreadyExistException;
import com.codedthoughts.codedthoughts.util.JwtTokenUtil;
import com.codedthoughts.codedthoughts.views.AuthRequestView;
import com.codedthoughts.codedthoughts.views.LoginResponseView;
import com.codedthoughts.codedthoughts.views.SignupRequestView;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public Optional<LoginResponseView> doLogin(AuthRequestView authRequestView) throws BadCredentialsException{
        Authentication authenticate = authenticate(authRequestView.getUsername(), authRequestView.getPassword());
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authenticate.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return Optional.of(LoginResponseView
                            .builder()
                            .userName(user.getUsername())
                            .token(jwtTokenUtil.generateJwtToken(authenticate))
                            .build());
    }

    public Optional<LoginResponseView> doSignup(SignupRequestView signupView) throws UserAlreadyExistException, BadCredentialsException {
        User newUser = User
                .builder()
                .userName(signupView.getUsername())
                .email(signupView.getEmail())
                .password(passwordEncoder.encode(signupView.getPassword()))
                .DOB(signupView.getDOB())
                .role(Role.USER)
                .build();
        userService.addUser(newUser);
        Authentication authentication = authenticate(signupView.getUsername(), signupView.getPassword());
        return Optional.of(LoginResponseView
                .builder()
                .userName(signupView.getUsername())
                .token(jwtTokenUtil.generateJwtToken(authentication))
                .build());

    }

    private Authentication authenticate(String username, String password) throws BadCredentialsException {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
