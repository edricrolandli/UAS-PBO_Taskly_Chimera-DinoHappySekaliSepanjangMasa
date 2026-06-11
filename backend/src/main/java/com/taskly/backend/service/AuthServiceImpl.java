package com.taskly.backend.service;

import com.taskly.backend.dto.*;
import com.taskly.backend.model.Role;
import com.taskly.backend.model.User;
import com.taskly.backend.repository.UserRepository;
import com.taskly.backend.security.JwtTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // KONSTRUKTUR MANUAL (Mengeliminasi ketergantungan pada Lombok Annotation Processor)
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        // Validasi password cocok
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Password dan konfirmasi tidak cocok");
        }
        // Cek email unik
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }
        // Buat user baru
        User user = new User(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password())
        );
        user.setRole(Role.ROLE_USER);   // default user
        user = userRepository.save(user);

        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Email atau password salah"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Email atau password salah");
        }
        String token = jwtTokenProvider.generateToken(user);
        return new JwtResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
