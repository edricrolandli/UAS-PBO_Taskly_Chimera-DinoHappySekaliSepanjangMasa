package com.taskly.backend.service;

import com.taskly.backend.dto.*;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
}