package com.articlehub.platform.impl;

import com.articlehub.dto.ArticleDTO;
import com.articlehub.platform.PlatformPublisher;
import com.articlehub.platform.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class JuejinPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("title", article.getTitle());
            data.put("content", article.getContent());
            data.put("tags", article.getTags());
            data.put("category", "backend");

            String url = "https://api.juejin.cn/v1/article/add";
            RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(data),
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + credentials)
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                    String articleId = (String) result.get("article_id");
                    return PublishResult.builder()
                        .success(true)
                        .publishedUrl(String.format("https://juejin.cn/post/%s", articleId))
                        .build();
                } else {
                    return PublishResult.builder()
                        .success(false)
                        .errorMessage("Failed to publish to Juejin")
                        .build();
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    @Override
    public String getPlatformName() {
        return "juejin";
    }
}
