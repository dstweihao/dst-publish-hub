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
public class CsdnPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("title", article.getTitle());
            data.put("markdownBody", article.getContent());
            data.put("tags", article.getTags());
            data.put("articleType", "original");

            String url = "https://openapi.csdn.net/article/publish";
            RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(data),
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", credentials)
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                    String articleId = (String) result.get("articleId");
                    return PublishResult.builder()
                        .success(true)
                        .publishedUrl(String.format("https://blog.csdn.net/%s/article/details/%s", accountId, articleId))
                        .build();
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
        return PublishResult.builder()
            .success(false)
            .errorMessage("Failed to publish to CSDN")
            .build();
    }

    @Override
    public String getPlatformName() {
        return "csdn";
    }
}
