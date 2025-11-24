package com.articlehub.controller;

import com.articlehub.dto.ApiResponse;
import com.articlehub.dto.AuthRequest;
import com.articlehub.dto.AuthResponse;
import com.articlehub.dto.UserDTO;
import com.articlehub.entity.User;
import com.articlehub.service.UserService;
import com.articlehub.util.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
        User user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword());
        String token = jwtTokenProvider.generateToken(user.getUsername());
        
        UserDTO userDTO = UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .avatar(user.getAvatar())
            .createdAt(user.getCreatedAt())
            .build();

        AuthResponse response = AuthResponse.builder()
            .token(token)
            .user(userDTO)
            .build();

        return ApiResponse.success("User registered successfully", response);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        User user = userService.getUserByEmail(request.getEmail());

        UserDTO userDTO = UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .avatar(user.getAvatar())
            .createdAt(user.getCreatedAt())
            .build();

        AuthResponse response = AuthResponse.builder()
            .token(token)
            .user(userDTO)
            .build();

        return ApiResponse.success("Login successful", response);
    }

    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);

        UserDTO userDTO = UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .avatar(user.getAvatar())
            .createdAt(user.getCreatedAt())
            .build();

        return ApiResponse.success(userDTO);
    }
}
