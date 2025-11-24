package com.articlehub.service;

import com.articlehub.dto.ArticleDTO;
import com.articlehub.entity.Article;
import com.articlehub.entity.PlatformAccount;
import com.articlehub.entity.PlatformPublish;
import com.articlehub.entity.User;
import com.articlehub.platform.PlatformPublisher;
import com.articlehub.platform.PlatformPublisherFactory;
import com.articlehub.platform.PublishResult;
import com.articlehub.repository.ArticleRepository;
import com.articlehub.repository.PlatformAccountRepository;
import com.articlehub.repository.PlatformPublishRepository;
import com.articlehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishService {
    private final ArticleRepository articleRepository;
    private final PlatformPublishRepository platformPublishRepository;
    private final PlatformAccountRepository platformAccountRepository;
    private final UserRepository userRepository;
    private final PlatformPublisherFactory publisherFactory;

    /**
     * 一键发布到多个平台
     */
    @Transactional
    public Map<String, PublishResult> publishToMultiplePlatforms(Long articleId, Long userId, List<String> platforms) {
        log.info("Publishing article {} to platforms: {}", articleId, platforms);

        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));

        if (!article.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to publish this article");
        }

        Map<String, PublishResult> results = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String platform : platforms) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    PublishResult result = publishToSinglePlatform(article, userId, platform);
                    results.put(platform, result);
                } catch (Exception e) {
                    log.error("Failed to publish to platform: {}", platform, e);
                    results.put(platform, PublishResult.builder()
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build());
                }
            });
            futures.add(future);
        }

        // 等待所有发布任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 更新文章状态
        article.setStatus("published");
        article.setPublishedAt(LocalDateTime.now());
        articleRepository.save(article);

        log.info("Article {} published successfully", articleId);
        return results;
    }

    /**
     * 发布到单个平台
     */
    @Transactional
    public PublishResult publishToSinglePlatform(Article article, Long userId, String platform) {
        log.info("Publishing article {} to platform: {}", article.getId(), platform);

        // 获取平台账号
        PlatformAccount platformAccount = platformAccountRepository
            .findByUserIdAndPlatform(userId, platform)
            .orElseThrow(() -> new RuntimeException("Platform account not found for: " + platform));

        // 检查账号是否激活
        if (!platformAccount.getIsActive()) {
            throw new RuntimeException("Platform account is not active");
        }

        // 转换Article为ArticleDTO
        ArticleDTO articleDTO = convertToDTO(article);

        // 获取对应平台的发布器
        PlatformPublisher publisher = publisherFactory.getPublisher(platform);

        // 执行发布
        PublishResult result;
        try {
            result = publisher.publish(platformAccount.getAccountId(), platformAccount.getCredentials(), articleDTO);
        } catch (Exception e) {
            log.error("Error publishing to platform: {}", platform, e);
            result = PublishResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }

        // 保存发布记录
        PlatformPublish platformPublish = PlatformPublish.builder()
            .article(article)
            .platformAccount(platformAccount)
            .platform(platform)
            .status(result.isSuccess() ? "published" : "failed")
            .publishedUrl(result.getPublishedUrl())
            .errorMessage(result.getErrorMessage())
            .publishedAt(result.isSuccess() ? LocalDateTime.now() : null)
            .build();

        platformPublishRepository.save(platformPublish);

        log.info("Article {} published to platform {}: {}", article.getId(), platform, result.isSuccess());
        return result;
    }

    /**
     * 获取文章的所有发布记录
     */
    public List<PlatformPublish> getPublishRecords(Long articleId) {
        return platformPublishRepository.findByArticleId(articleId);
    }

    /**
     * 获取发布统计信息
     */
    public PublishStatistics getPublishStatistics(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        long totalArticles = articleRepository.countByUser(user);
        long publishedArticles = articleRepository.countByUserAndStatus(user, "published");
        long totalPublishes = platformPublishRepository.countByArticleUserAndStatus(user, "published");

        return PublishStatistics.builder()
            .totalArticles(totalArticles)
            .publishedArticles(publishedArticles)
            .totalPublishes(totalPublishes)
            .successRate(totalPublishes > 0 ? 
                (float) platformPublishRepository.countByArticleUserAndStatus(user, "published") / 
                (float) platformPublishRepository.countByArticleUser(user) * 100 : 0)
            .build();
    }

    /**
     * 重新发布失败的文章
     */
    @Transactional
    public Map<String, PublishResult> retryFailedPublishes(Long articleId, Long userId) {
        List<PlatformPublish> failedPublishes = platformPublishRepository
            .findByArticleIdAndStatus(articleId, "failed");

        if (failedPublishes.isEmpty()) {
            throw new RuntimeException("No failed publishes to retry");
        }

        Article article = articleRepository.findById(articleId)
            .orElseThrow(() -> new RuntimeException("Article not found"));

        List<String> platforms = failedPublishes.stream()
            .map(PlatformPublish::getPlatform)
            .collect(Collectors.toList());

        return publishToMultiplePlatforms(articleId, userId, platforms);
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
            .build();
    }
}
