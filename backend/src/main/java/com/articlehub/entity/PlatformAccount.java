package com.articlehub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String platform;
    private String username;
    private String accountId;
    private String credentials;
    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private boolean isActive = true;

    private LocalDateTime lastUsed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}