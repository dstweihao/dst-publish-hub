package com.articlehub.platform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishResult {
    private boolean success;
    private String publishedUrl;
    private String errorMessage;
}
