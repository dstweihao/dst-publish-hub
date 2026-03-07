package com.articlehub.controller;

import com.articlehub.dto.ApiResponse;
import com.articlehub.entity.Article;
import com.articlehub.repository.ArticleRepository;
import com.articlehub.service.AiService;
import com.articlehub.service.AiService.AiPublishSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AiService aiService;
    private final ArticleRepository articleRepository;

    /**
     * 根据文章 ID 生成 AI 发布设置建议
     */
    @PostMapping("/generate-publish-settings")
    public ResponseEntity<ApiResponse<AiPublishSettings>> generateSettings(
            @RequestParam Long articleId) {
        log.info("AI 发布设置生成请求 - articleId: {}", articleId);

        Optional<Article> articleOpt = articleRepository.findById(articleId);
        if (articleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("文章不存在"));
        }

        Article article = articleOpt.get();
        AiPublishSettings settings = aiService.generatePublishSettings(
                article.getTitle(),
                article.getContent() != null ? article.getContent() : ""
        );

        return ResponseEntity.ok(ApiResponse.success("AI 设置生成成功", settings));
    }

    /**
     * 根据传入的文章内容直接生成 AI 发布设置（无需存库）
     */
    @PostMapping("/generate-from-content")
    public ResponseEntity<ApiResponse<AiPublishSettings>> generateFromContent(
            @RequestBody GenerateRequest req) {
        log.info("AI 发布设置生成请求（直接内容）- title: {}", req.title());
        AiPublishSettings settings = aiService.generatePublishSettings(req.title(), req.content());
        return ResponseEntity.ok(ApiResponse.success("AI 设置生成成功", settings));
    }

    public record GenerateRequest(String title, String content) {}
}
