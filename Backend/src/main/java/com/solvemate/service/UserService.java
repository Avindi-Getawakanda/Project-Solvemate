package com.solvemate.service;

import com.solvemate.dto.RegisterRequest;
import com.solvemate.dto.LoginRequest;
import com.solvemate.dto.LoginResponse;
import com.solvemate.dto.ApiResponse;

public interface UserService {

    ApiResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    ApiResponse logout();
}