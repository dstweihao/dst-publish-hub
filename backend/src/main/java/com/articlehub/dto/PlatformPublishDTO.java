package com.articlehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformPublishDTO {
    private Long id;
    private String platform;
    private String status;
    private String publishedUrl;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}
