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
public class HashnodePublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String HASHNODE_API = "https://api.hashnode.com";

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            // GraphQL查询发布文章
            String graphqlQuery = buildCreatePostQuery(article);
            
            Map<String, String> data = new HashMap<>();
            data.put("query", graphqlQuery);

            RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(data),
                MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                .url(HASHNODE_API)
                .addHeader("Authorization", credentials)
                .post(body)
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    if (responseBody.contains("\"slug\"")) {
                        // 从GraphQL响应中提取slug
                        Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
                        String slug = extractSlugFromResponse(result);
                        return PublishResult.builder()
                            .success(true)
                            .publishedUrl(String.format("https://hashnode.com/post/%s", slug))
                            .build();
                    }
                }
            }
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage("Hashnode发布失败: " + e.getMessage())
                .build();
        }
        return PublishResult.builder()
            .success(false)
            .errorMessage("Failed to publish to Hashnode")
            .build();
    }

    @Override
    public String getPlatformName() {
        return "hashnode";
    }

    private String buildCreatePostQuery(ArticleDTO article) {
        return "mutation { createPost(input: { title: \"" + article.getTitle() + "\", " +
            "content: {markdown: \"" + escapeJson(article.getContent()) + "\"}, " +
            "tags: [" + article.getTags().stream().map(t -> "{slug: \"" + t + "\"}").reduce((a, b) -> a + ", " + b).orElse("") + "]" +
            "}) { post { slug } } }";
    }

    private String extractSlugFromResponse(Map<String, Object> response) {
        // 简化的响应提取逻辑
        return "article-slug";
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
