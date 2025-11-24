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
public class DoubanPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DOUBAN_API = "https://api.douban.com/v2";

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            // 豆瓣相册或日记API发布电影观后感
            Map<String, Object> data = new HashMap<>();
            data.put("title", article.getTitle());
            data.put("content", article.getContent());
            data.put("tags", String.join(",", article.getTags()));
            data.put("type", "movie_review"); // 电影观后感类型

            String url = DOUBAN_API + "/user/" + accountId + "/notes";
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
                    String id = String.valueOf(result.get("id"));
                    return PublishResult.builder()
                        .success(true)
                        .publishedUrl(String.format("https://www.douban.com/note/%s/", id))
                        .build();
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage("豆瓣发布失败: " + e.getMessage())
                .build();
        }
        return PublishResult.builder()
            .success(false)
            .errorMessage("Failed to publish to Douban")
            .build();
    }

    @Override
    public String getPlatformName() {
        return "douban";
    }
}
