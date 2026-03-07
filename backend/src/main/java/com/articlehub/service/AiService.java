package com.articlehub.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI 辅助发布设置服务
 * 调用大语言模型（默认通义千问），根据文章内容自动生成摘要和标签
 */
@Service
@Slf4j
public class AiService {

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Value("${ai.model:qwen-plus}")
    private String model;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record AiPublishSettings(String summary, List<String> tags, String topic) {}

    /**
     * 根据文章内容生成发布设置建议
     */
    public AiPublishSettings generatePublishSettings(String title, String content) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI API Key 未配置，返回默认值");
            return new AiPublishSettings("（请配置 AI API Key 后使用）", List.of(), "");
        }

        // 提取纯文本（去掉 HTML 标签）
        String plainContent = content != null
                ? content.replaceAll("<[^>]+>", "").replaceAll("&nbsp;", " ").trim()
                : "";

        // 截取前 2000 字避免 token 过多
        if (plainContent.length() > 2000) {
            plainContent = plainContent.substring(0, 2000) + "...";
        }

        String prompt = """
                请根据以下文章内容，生成适合在知乎/掘金等平台发布的设置。
                
                标题：%s
                
                正文摘要：%s
                
                请以 JSON 格式返回，包含以下字段：
                - summary: 文章摘要（100字以内，吸引读者点击）
                - tags: 标签数组（3-5个，字符串数组）
                - topic: 推荐发布话题/专栏（一个最相关的分类）
                
                只返回 JSON，不要其他内容。
                """.formatted(title, plainContent);

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)
            ));

            String json = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(baseUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String respBody = response.body() != null ? response.body().string() : "";
                log.debug("AI API 响应: {}", respBody);

                if (!response.isSuccessful()) {
                    log.error("AI API 调用失败: {} {}", response.code(), respBody);
                    return new AiPublishSettings("AI 生成失败，请手动填写", List.of(), "");
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> respMap = objectMapper.readValue(respBody, Map.class);
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) respMap.get("choices");
                if (choices == null || choices.isEmpty()) {
                    return new AiPublishSettings("", List.of(), "");
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                String aiContent = (String) message.get("content");

                // 提取 JSON 内容（AI 有时会加 markdown 代码块）
                aiContent = aiContent.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

                @SuppressWarnings("unchecked")
                Map<String, Object> settings = objectMapper.readValue(aiContent, Map.class);
                String summary = (String) settings.getOrDefault("summary", "");
                @SuppressWarnings("unchecked")
                List<String> tags = (List<String>) settings.getOrDefault("tags", List.of());
                String topic = (String) settings.getOrDefault("topic", "");

                return new AiPublishSettings(summary, tags, topic);
            }
        } catch (Exception e) {
            log.error("AI 发布设置生成异常", e);
            return new AiPublishSettings("AI 生成异常: " + e.getMessage(), List.of(), "");
        }
    }
}
