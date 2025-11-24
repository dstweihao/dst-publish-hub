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
public class WechatPublisher implements PlatformPublisher {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        try {
            // 1. 获取微信临时TOKEN
            String accessToken = getWechatAccessToken(credentials);

            // 2. 准备图文消息
            Map<String, Object> newsItem = new HashMap<>();
            newsItem.put("title", article.getTitle());
            newsItem.put("author", "ArticleHub");
            newsItem.put("digest", article.getDescription());
            newsItem.put("show_cover_pic", 1);
            newsItem.put("content", convertMarkdownToHtml(article.getContent()));
            newsItem.put("content_source_url", "https://articlehub.com");

            // 3. 调用微信API发布
            String mediaId = uploadNewsToWechat(accessToken, newsItem);
            String url = publishNewsToWechat(accessToken, mediaId);

            return PublishResult.builder()
                .success(true)
                .publishedUrl(url)
                .build();
        } catch (Exception e) {
            return PublishResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    @Override
    public String getPlatformName() {
        return "wechat";
    }

    private String getWechatAccessToken(String credentials) throws IOException {
        // 解析credentials获取AppID和AppSecret
        // 实际实现需要调用微信API获取token
        return "mock_access_token";
    }

    private String uploadNewsToWechat(String accessToken, Map<String, Object> newsItem) throws IOException {
        String url = String.format("https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=%s", accessToken);
        RequestBody body = RequestBody.create(
            objectMapper.writeValueAsString(newsItem),
            MediaType.get("application/json; charset=utf-8")
        );
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Map<String, Object> result = objectMapper.readValue(response.body().string(), Map.class);
                return (String) result.get("media_id");
            }
        }
        throw new IOException("Failed to upload news to wechat");
    }

    private String publishNewsToWechat(String accessToken, String mediaId) throws IOException {
        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s", accessToken);
        Map<String, Object> data = new HashMap<>();
        data.put("media_id", mediaId);
        data.put("msgtype", "mpnews");

        RequestBody body = RequestBody.create(
            objectMapper.writeValueAsString(data),
            MediaType.get("application/json; charset=utf-8")
        );
        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return String.format("https://mp.weixin.qq.com/article?%s", mediaId);
            }
        }
        throw new IOException("Failed to publish to wechat");
    }

    private String convertMarkdownToHtml(String markdown) {
        // 简单的Markdown到HTML转换
        // 实际项目可以使用commonmark-java库
        return markdown
            .replaceAll("^### (.*?)$", "<h3>$1</h3>")
            .replaceAll("^## (.*?)$", "<h2>$1</h2>")
            .replaceAll("^# (.*?)$", "<h1>$1</h1>")
            .replaceAll("^\\*\\*\\*$", "<hr/>")
            .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
            .replaceAll("\\*(.*?)\\*", "<em>$1</em>")
            .replaceAll("\\n", "<br/>");
    }
}
