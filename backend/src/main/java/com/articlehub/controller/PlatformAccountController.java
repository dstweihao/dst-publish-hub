package com.articlehub.controller;

import com.articlehub.dto.ApiResponse;
import com.articlehub.dto.PlatformAccountDTO;
import com.articlehub.dto.PlatformAccountRequest;
import com.articlehub.entity.PlatformAccount;
import com.articlehub.entity.User;
import com.articlehub.repository.PlatformAccountRepository;
import com.articlehub.repository.UserRepository;
import com.articlehub.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/platforms")
public class PlatformAccountController {

    @Autowired
    private PlatformAccountRepository platformAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    private User getAuthenticatedUser(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String email = tokenProvider.getUsernameFromJWT(token); // Actually gets email
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        throw new RuntimeException("Unauthorized");
    }

    private PlatformAccountDTO convertToDTO(PlatformAccount account) {
        PlatformAccountDTO dto = new PlatformAccountDTO();
        dto.setId(account.getId());
        dto.setPlatform(account.getPlatform());
        dto.setUsername(account.getUsername());
        dto.setAccessToken(account.getAccessToken());
        dto.setActive(account.isActive());
        return dto;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlatformAccountDTO>>> getPlatforms(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        List<PlatformAccount> accounts = platformAccountRepository.findByUserId(user.getId());
        
        List<PlatformAccountDTO> dtos = accounts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(ApiResponse.success("Platforms fetched successfully", dtos));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlatformAccountDTO>> savePlatform(
            @RequestBody PlatformAccountRequest platformRequest,
            HttpServletRequest request) {
        User user = getAuthenticatedUser(request);

        // Check if platform exists for user
        Optional<PlatformAccount> existing = platformAccountRepository.findByUserIdAndPlatform(user.getId(), platformRequest.getPlatform());
        
        PlatformAccount account;
        if (existing.isPresent()) {
            account = existing.get();
            account.setUsername(platformRequest.getUsername());
            account.setAccessToken(platformRequest.getAccessToken());
            account.setActive(true);
            account.setUpdatedAt(LocalDateTime.now());
        } else {
            account = PlatformAccount.builder()
                    .user(user)
                    .platform(platformRequest.getPlatform())
                    .username(platformRequest.getUsername())
                    .accessToken(platformRequest.getAccessToken())
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
        
        PlatformAccount saved = platformAccountRepository.save(account);
        return ResponseEntity.ok(ApiResponse.success("Platform saved successfully", convertToDTO(saved)));
    }

    @DeleteMapping("/{platformName}")
    public ResponseEntity<ApiResponse<Void>> deletePlatform(
            @PathVariable String platformName,
            HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        
        Optional<PlatformAccount> existing = platformAccountRepository.findByUserIdAndPlatform(user.getId(), platformName);
        if (existing.isPresent()) {
            platformAccountRepository.delete(existing.get());
            return ResponseEntity.ok(ApiResponse.success("Platform deleted successfully", null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Platform not found"));
        }
    }
}
