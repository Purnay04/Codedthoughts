package com.codedthoughts.codedthoughts.repo;

import java.util.List;
import java.util.Optional;

import com.codedthoughts.codedthoughts.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByUserName(String userName);
    public Optional<User> findByEmail(String email);
}
