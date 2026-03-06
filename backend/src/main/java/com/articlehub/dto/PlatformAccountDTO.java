package com.articlehub.dto;

import lombok.Data;

@Data
public class PlatformAccountDTO {
    private Long id;
    private String platform;
    private String username;
    private String accessToken;
    private boolean isActive;
}
