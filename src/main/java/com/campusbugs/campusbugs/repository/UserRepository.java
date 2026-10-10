package com.campusbugs.campusbugs.repository;

import com.campusbugs.campusbugs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRollNumber(String rollNumber);
}
