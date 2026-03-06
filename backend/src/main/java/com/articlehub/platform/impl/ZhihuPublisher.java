package com.articlehub.platform.impl;

import com.articlehub.dto.ArticleDTO;
import com.articlehub.platform.PlatformPublisher;
import com.articlehub.platform.PublishResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 知乎平台发布实现
 */
@Service
@Slf4j
public class ZhihuPublisher implements PlatformPublisher {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .followRedirects(true)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        log.info("开始执行知乎真实发布流程 - 账号: {}", accountId);
        try {
            // 1. 提取 XSRF Token (知乎要求 Header 中带 x-xsrf-token)
            String xsrf = extractXsrf(credentials);
            if (xsrf == null || xsrf.isEmpty()) {
                // 如果 Cookie 中没有，尝试请求一次首页获取（或者使用默认占位，有时候知乎允许不严格校验）
                xsrf = "6ca2a9f5-446b-4e6f-8a0a-be7213214531"; // 兜底
            }

            // 2. 构造发布参数
            // 注意：知乎专栏发布接口需要的是 HTML 内容，通常前端已经渲染好或后端需要转换
            // 这里我们假设 content 已经是 HTML（由前端 Editor 决定）
            Map<String, Object> payload = new HashMap<>();
            payload.put("title", article.getTitle());
            payload.put("content", article.getContent());
            
            // 默认发布到用户的自动生成的专栏或需要指定 column ID
            // 实际工程中应该先请求 https://www.zhihu.com/api/v4/me/column-contributions 获取专栏列表
            // 为了演示，我们尝试直接发布到用户主页（如果 API 支持）或使用一个占位 Column
            // 很多时候如果不传 column，会发布为“个人文章”
            
            String jsonPayload = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(jsonPayload, MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url("https://zhuanlan.zhihu.com/api/articles")
                    .addHeader("Cookie", credentials)
                    .addHeader("x-xsrf-token", xsrf)
                    .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .addHeader("Referer", "https://zhuanlan.zhihu.com/write")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                log.info("知乎发布响应: code={}, body={}", response.code(), responseBody);

                if (response.isSuccessful()) {
                    // 解析响应获取文章 ID
                    Map<String, Object> respMap = objectMapper.readValue(responseBody, Map.class);
                    Object id = respMap.get("id");
                    String publishedUrl = "https://zhuanlan.zhihu.com/p/" + id;
                    
                    return PublishResult.builder()
                            .success(true)
                            .publishedUrl(publishedUrl)
                            .build();
                } else {
                    return PublishResult.builder()
                            .success(false)
                            .errorMessage("知乎发布失败: " + response.code() + " " + responseBody)
                            .build();
                }
            }

        } catch (Exception e) {
            log.error("知乎发布异常", e);
            return PublishResult.builder()
                    .success(false)
                    .errorMessage("知乎发布异常: " + e.getMessage())
                    .build();
        }
    }

    private String extractXsrf(String cookie) {
        if (cookie == null) return null;
        Pattern pattern = Pattern.compile("_xsrf=([^;]+)");
        Matcher matcher = pattern.matcher(cookie);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    public String getPlatformName() {
        return "知乎";
    }
}
