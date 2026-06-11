package com.taskly.backend.controller;

import com.taskly.backend.dto.JwtResponse;
import com.taskly.backend.dto.LoginRequest;
import com.taskly.backend.dto.RegisterRequest;
import com.taskly.backend.dto.UserResponse;
import com.taskly.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final AuthService authService;

    // KONSTRUKTUR MANUAL (Mengatasi Error 'variable authService not initialized')
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
