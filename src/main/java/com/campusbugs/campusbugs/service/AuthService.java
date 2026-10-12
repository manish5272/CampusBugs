package com.campusbugs.campusbugs.service;

import com.campusbugs.campusbugs.dto.LoginRequest;
import com.campusbugs.campusbugs.dto.RegisterRequest;
import com.campusbugs.campusbugs.model.Role;
import com.campusbugs.campusbugs.model.User;
import com.campusbugs.campusbugs.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
        }

        public void register(RegisterRequest request) {

            String roll = request.rollNumber().trim().toUpperCase();

            if (userRepository.findByRollNumber(roll).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Roll number is already present");

            }

            User user = new User();
            user.setRollNumber(roll);
            user.setPasswordHash(passwordEncoder.encode(request.password()));
            user.setRole(Role.STUDENT);
            userRepository.save(user);
        }

        public void login(LoginRequest request){
            String roll = request.rollNumber().trim().toUpperCase();

            User user = userRepository.findByRollNumber(roll)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,"INVALID ROLL NUMBER OR PASSWORD"));

            if(!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,"Invalid roll number or password");
            }

        }

}
