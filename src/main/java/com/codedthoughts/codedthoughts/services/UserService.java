package com.codedthoughts.codedthoughts.services;

import com.codedthoughts.codedthoughts.entities.User;
import com.codedthoughts.codedthoughts.exceptions.UserAlreadyExistException;
import com.codedthoughts.codedthoughts.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true, rollbackFor = UsernameNotFoundException.class)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = getUserByUserName(userName);
        return new org.springframework.security.core.userdetails.User(
                userName,
                user.getPassword(),
                user.getAuthorities()
        );
    }

    @Transactional
    public User getUserByUserName(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not exist with " + username));
    }

    @Transactional(rollbackFor = UserAlreadyExistException.class)
    public void addUser(User user) throws UserAlreadyExistException {
        if(userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UserAlreadyExistException(user.getEmail());
        else if(userRepository.findByUserName(user.getUsername()).isPresent())
            throw new UserAlreadyExistException(user.getUsername());
        userRepository.save(user);
    }
}
