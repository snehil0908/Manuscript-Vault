package com.manuscriptvault.service;

import com.manuscriptvault.config.JwtUtil;
import com.manuscriptvault.dto.AuthDTOs.*;
import com.manuscriptvault.model.User;
import com.manuscriptvault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService - Business logic for user authentication and registration.
 * Handles login validation and signup with BCrypt password hashing.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authenticate a user with username and password.
     * Returns a JWT token on success.
     */
    public AuthResponse login(LoginRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Verify password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token with username and role
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResponse(
            token,
            user.getUsername(),
            user.getRole().name(),
            "Login successful"
        );
    }

    /**
     * Register a new user.
     * Validates uniqueness, hashes password, and saves to DB.
     */
    public ApiResponse signup(SignupRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().length() < 3) {
            throw new RuntimeException("Username must be at least 3 characters");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        // Check if username already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken. Please choose another.");
        }

        // Parse role (default to USER if not specified)
        User.Role role;
        try {
            role = User.Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            role = User.Role.USER;
        }

        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create and save user
        User newUser = new User(request.getUsername(), hashedPassword, role);
        userRepository.save(newUser);

        return new ApiResponse(true, "Account created successfully! You can now log in.");
    }
}
