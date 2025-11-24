package com.articlehub.platform.impl;

import com.articlehub.dto.ArticleDTO;
import com.articlehub.platform.PlatformPublisher;
import com.articlehub.platform.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DevtoPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DEVTO_API = "https://dev.to/api";

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            Map<String, Object> articleData = new HashMap<>();
            Map<String, Object> articleContent = new HashMap<>();
            
            articleContent.put("title", article.getTitle());
            articleContent.put("body_markdown", article.getContent());
            articleContent.put("tags", article.getTags());
            articleContent.put("published", true);
            
            articleData.put("article", articleContent);

            String url = DEVTO_API + "/articles";
            RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(articleData),
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(url)
                .addHeader("api-key", credentials)
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                    String url_str = (String) result.get("url");
                    return PublishResult.builder()
                        .success(true)
                        .publishedUrl(url_str)
                        .build();
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage("Dev.to发布失败: " + e.getMessage())
                .build();
        }
        return PublishResult.builder()
            .success(false)
            .errorMessage("Failed to publish to Dev.to")
            .build();
    }

    @Override
    public String getPlatformName() {
        return "devto";
    }
}
