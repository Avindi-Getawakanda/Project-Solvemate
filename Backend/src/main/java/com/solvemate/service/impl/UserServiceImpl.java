package com.solvemate.service.impl;

import com.solvemate.dto.*;
import com.solvemate.model.User;
import com.solvemate.repository.UserRepository;
import com.solvemate.service.UserService;
import com.solvemate.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);
        return new ApiResponse("User registered successfully");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid password");
        }

        LoginResponse response = new LoginResponse();
        response.setMessage("Login successful");
        response.setRole(user.getRole());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());        // to make sure email is set
        return response;
    }

    @Override
    public ApiResponse logout() {
        return new ApiResponse("Logged out successfully");
    }
}