package com.codedthoughts.codedthoughts.services;

import com.codedthoughts.codedthoughts.entities.User;
import com.codedthoughts.codedthoughts.exceptions.UserAlreadyExistException;
import com.codedthoughts.codedthoughts.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository
                .findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not exist with " + userName));
        return new org.springframework.security.core.userdetails.User(
                userName,
                user.getPassword(),
                user.getAuthorities()
        );
    }

    public void addUser(User user) throws UserAlreadyExistException {
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UserAlreadyExistException(user.getEmail());
        userRepository.save(user);
    }
}
