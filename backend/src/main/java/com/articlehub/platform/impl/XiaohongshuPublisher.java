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
public class XiaohongshuPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String XHS_API = "https://api.xiaohongshu.com/api/sns/v1";

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("title", article.getTitle());
            data.put("desc", article.getContent());
            data.put("type", "note");
            data.put("topic_list", article.getTags());
            
            String url = XHS_API + "/feed";
            RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(data),
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + credentials)
                .addHeader("User-Agent", "ArticleHub/1.0")
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                    String id = (String) result.get("note_id");
                    return PublishResult.builder()
                        .success(true)
                        .publishedUrl(String.format("https://www.xiaohongshu.com/explore/%s", id))
                        .build();
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage("小红书发布失败: " + e.getMessage())
                .build();
        }
        return PublishResult.builder()
            .success(false)
            .errorMessage("Failed to publish to Xiaohongshu")
            .build();
    }

    @Override
    public String getPlatformName() {
        return "xiaohongshu";
    }
}
