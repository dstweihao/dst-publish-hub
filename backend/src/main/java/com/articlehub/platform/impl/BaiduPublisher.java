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
public class BaiduPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BAIDU_API = "https://openapi.baidu.com/api/content/upload";

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("title", article.getTitle());
            params.put("content", article.getContent());
            params.put("tags", String.join(",", article.getTags()));
            params.put("access_token", credentials);

            // 构建表单请求
            FormBody.Builder formBuilder = new FormBody.Builder();
            params.forEach(formBuilder::add);

            Request request = new Request.Builder()
                .url(BAIDU_API)
                .post(formBuilder.build())
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                    if (result.containsKey("request_id")) {
                        String id = (String) result.get("request_id");
                        return PublishResult.builder()
                            .success(true)
                            .publishedUrl(String.format("https://baijiahao.baidu.com/s?id=%s", id))
                            .build();
                    }
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage("百家号发布失败: " + e.getMessage())
                .build();
        }
        return PublishResult.builder()
            .success(false)
            .errorMessage("Failed to publish to Baidu Baijia")
            .build();
    }

    @Override
    public String getPlatformName() {
        return "baidu";
    }
}
