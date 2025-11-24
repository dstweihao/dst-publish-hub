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
public class WeiboPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String WEIBO_API = "https://api.weibo.com/2";

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            // 微博发布长文章（文章链接）
            String content = String.format("%s\n%s", 
                article.getTitle(), 
                truncateContent(article.getContent(), 140)
            );

            Map<String, String> params = new HashMap<>();
            params.put("status", content);
            params.put("access_token", credentials);
            params.put("visible", "0");

            FormBody.Builder formBuilder = new FormBody.Builder();
            params.forEach(formBuilder::add);

            Request request = new Request.Builder()
                .url(WEIBO_API + "/statuses/update.json")
                .post(formBuilder.build())
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                    String id = String.valueOf(result.get("id"));
                    String uid = String.valueOf(result.get("user"));
                    return PublishResult.builder()
                        .success(true)
                        .publishedUrl(String.format("https://weibo.com/%s/%s", uid, id))
                        .build();
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage("微博发布失败: " + e.getMessage())
                .build();
        }
        return PublishResult.builder()
            .success(false)
            .errorMessage("Failed to publish to Weibo")
            .build();
    }

    @Override
    public String getPlatformName() {
        return "weibo";
    }

    private String truncateContent(String content, int maxLength) {
        if (content.length() > maxLength) {
            return content.substring(0, maxLength) + "...";
        }
        return content;
    }
}
