package com.articlehub.dto;

import lombok.Data;

@Data
public class PlatformAccountRequest {
    private String platform;
    private String username;
    private String accessToken;
}
