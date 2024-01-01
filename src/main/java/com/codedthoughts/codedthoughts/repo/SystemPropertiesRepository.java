package com.codedthoughts.codedthoughts.repo;

import com.codedthoughts.codedthoughts.entities.SystemProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemPropertiesRepository extends JpaRepository<SystemProperties, Integer> {
    public Optional<SystemProperties> findByName(String propName);
}
