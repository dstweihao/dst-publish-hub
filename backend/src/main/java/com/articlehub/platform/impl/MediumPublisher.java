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
public class MediumPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MEDIUM_API = "https://api.medium.com/v1";

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            // 获取Medium用户信息
            String userId = getMediumUserId(credentials);
            
            Map<String, Object> data = new HashMap<>();
            data.put("title", article.getTitle());
            data.put("contentFormat", "markdown");
            data.put("content", article.getContent());
            data.put("publishStatus", "public");
            data.put("tags", article.getTags());
            
            String url = String.format("%s/users/%s/posts", MEDIUM_API, userId);
            RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(data),
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + credentials)
                .addHeader("Accept", "application/json")
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                    String postId = (String) result.get("id");
                    return PublishResult.builder()
                        .success(true)
                        .publishedUrl(String.format("https://medium.com/p/%s", postId))
                        .build();
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage("Medium发布失败: " + e.getMessage())
                .build();
        }
        return PublishResult.builder()
            .success(false)
            .errorMessage("Failed to publish to Medium")
            .build();
    }

    @Override
    public String getPlatformName() {
        return "medium";
    }

    private String getMediumUserId(String credentials) throws Exception {
        Request request = new Request.Builder()
            .url(MEDIUM_API + "/me")
            .addHeader("Authorization", "Bearer " + credentials)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                Map<String, Object> data = (Map<String, Object>) result.get("data");
                return (String) data.get("id");
            }
        }
        throw new Exception("Failed to get Medium user ID");
    }
}
