package com.articlehub.controller;

import com.articlehub.dto.ApiResponse;
import com.articlehub.dto.PasswordUpdateRequest;
import com.articlehub.dto.UserDTO;
import com.articlehub.entity.User;
import com.articlehub.repository.UserRepository;
import com.articlehub.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private User getAuthenticatedUser(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String email = tokenProvider.getUsernameFromJWT(token);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Unauthorized");
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @RequestBody UserDTO updateRequest,
            HttpServletRequest request) {
        
        User user = getAuthenticatedUser(request);
        
        // Check if email is being changed and if it's already taken
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Email is already in use"));
            }
            user.setEmail(updateRequest.getEmail());
        }

        // Check if username is being changed and if it's already taken
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username is already in use"));
            }
            user.setUsername(updateRequest.getUsername());
        }

        User saved = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", convertToDTO(saved)));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody PasswordUpdateRequest passwordRequest,
            HttpServletRequest request) {
        
        User user = getAuthenticatedUser(request);

        // Verify current password
        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Incorrect current password"));
        }

        // Encrypt and set new password
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }
}
