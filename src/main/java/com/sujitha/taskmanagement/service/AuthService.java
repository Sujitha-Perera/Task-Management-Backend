package com.sujitha.taskmanagement.service;

import com.sujitha.taskmanagement.dto.AuthResponse;
import com.sujitha.taskmanagement.dto.LoginRequest;
import com.sujitha.taskmanagement.dto.RegisterRequest;
import com.sujitha.taskmanagement.entity.User;
import com.sujitha.taskmanagement.enums.Role;
import com.sujitha.taskmanagement.repository.UserRepository;
import com.sujitha.taskmanagement.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Register new user
    public String register(RegisterRequest request) {

        String email = request.getEmail().trim();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword())) // encrypted password
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    // Login user
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        boolean passwordMatch = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!passwordMatch) {
            throw new RuntimeException("Invalid email or password");
        }

        // generate JWT token
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }
}