package com.taskly.backend.controller;

import com.taskly.backend.model.User;
import com.taskly.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    // Dependency Injection melalui Konstruktor (Praktik OOP Terbaik)
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Ambil semua data user dari database
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Tambah user baru ke database
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
