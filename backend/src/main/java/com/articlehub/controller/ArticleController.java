package com.articlehub.controller;

import com.articlehub.dto.ApiResponse;
import com.articlehub.dto.ArticleDTO;
import com.articlehub.entity.Article;
import com.articlehub.entity.User;
import com.articlehub.service.ArticleService;
import com.articlehub.service.UserService;
import com.articlehub.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ApiResponse<?> getArticles(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);
        Page<Article> articles = articleService.getUserArticles(user.getId(), page, size);
        return ApiResponse.success(articles);
    }

    @GetMapping("/{id}")
    public ApiResponse<ArticleDTO> getArticle(@PathVariable Long id) {
        Article article = articleService.getArticle(id);
        ArticleDTO dto = convertToDTO(article);
        return ApiResponse.success(dto);
    }

    @PostMapping
    public ApiResponse<ArticleDTO> createArticle(
            @RequestHeader("Authorization") String token,
            @RequestBody ArticleDTO articleDTO) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);
        Article article = articleService.createArticle(user.getId(), articleDTO);
        return ApiResponse.success("Article created successfully", convertToDTO(article));
    }

    @PutMapping("/{id}")
    public ApiResponse<ArticleDTO> updateArticle(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody ArticleDTO articleDTO) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);
        Article article = articleService.updateArticle(id, user.getId(), articleDTO);
        return ApiResponse.success("Article updated successfully", convertToDTO(article));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteArticle(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);
        articleService.deleteArticle(id, user.getId());
        return ApiResponse.success("Article deleted successfully");
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<ArticleDTO> publishArticle(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) PublishRequest request) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);
        Article article = articleService.publishArticle(id, user.getId());
        // TODO: 调用平台发布服务
        return ApiResponse.success("Article published successfully", convertToDTO(article));
    }

    private ArticleDTO convertToDTO(Article article) {
        return ArticleDTO.builder()
            .id(article.getId())
            .title(article.getTitle())
            .content(article.getContent())
            .description(article.getDescription())
            .theme(article.getTheme())
            .tags(article.getTags())
            .status(article.getStatus())
            .viewCount(article.getViewCount())
            .createdAt(article.getCreatedAt())
            .updatedAt(article.getUpdatedAt())
            .publishedAt(article.getPublishedAt())
            .build();
    }

    public static class PublishRequest {
        public java.util.List<String> platforms;
    }
}
