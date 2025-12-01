package com.articlehub.service;

import com.articlehub.dto.ArticleDTO;
import com.articlehub.entity.Article;
import com.articlehub.entity.User;
import com.articlehub.repository.ArticleRepositoryExtension;
import com.articlehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepositoryExtension articleRepositoryExtension;
    private final UserRepository userRepository;

    @Transactional
    public Article createArticle(Long userId, ArticleDTO articleDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Article article = Article.builder()
            .user(user)
            .title(articleDTO.getTitle())
            .content(articleDTO.getContent())
            .description(articleDTO.getDescription())
            .theme(articleDTO.getTheme() != null ? articleDTO.getTheme() : "default")
            .tags(articleDTO.getTags())
            .status("draft")
            .build();

        return articleRepositoryExtension.save(article);
    }

    @Transactional
    public Article updateArticle(Long articleId, Long userId, ArticleDTO articleDTO) {
        Article article = articleRepositoryExtension.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        article.setTitle(articleDTO.getTitle());
        article.setContent(articleDTO.getContent());
        article.setDescription(articleDTO.getDescription());
        article.setTheme(articleDTO.getTheme());
        article.setTags(articleDTO.getTags());

        return articleRepositoryExtension.save(article);
    }

    @Transactional
    public void deleteArticle(Long articleId, Long userId) {
        Article article = articleRepositoryExtension.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        articleRepositoryExtension.delete(article);
    }

    public Article getArticle(Long articleId) {
        return articleRepositoryExtension.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    public Page<Article> getUserArticles(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return articleRepositoryExtension.findByUser(user, pageable);
    }

    @Transactional
    public Article publishArticle(Long articleId, Long userId) {
        Article article = articleRepositoryExtension.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        article.setStatus("published");
        article.setPublishedAt(LocalDateTime.now());
        article.setViewCount(0);

        return articleRepositoryExtension.save(article);
    }
}
