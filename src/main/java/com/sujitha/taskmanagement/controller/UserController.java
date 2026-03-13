package com.sujitha.taskmanagement.controller;

import com.sujitha.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("/users")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<Map<String, String>>> getAllUsers() {
    List<Map<String, String>> users = userRepository.findAll().stream()
        .map(user -> Map.of(
            "email", user.getEmail(),
            "name", user.getName(),
            "role", user.getRole().name()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(users);
  }
}
