package com.articlehub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_publishes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformPublish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "platform_account_id", nullable = false)
    private PlatformAccount platformAccount;

    private String platform;
    private String platformArticleId;
    private String platformUrl;
    private String status;

    @Builder.Default
    private LocalDateTime publishedAt = LocalDateTime.now();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;

    public String getPublishedUrl() {
        return platformUrl;
    }
}