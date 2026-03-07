package com.articlehub.service;

import com.articlehub.entity.Article;
import com.articlehub.entity.PlatformAccount;
import com.articlehub.repository.ArticleRepository;
import com.articlehub.repository.PlatformAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时发布服务：每分钟扫描 scheduledAt <= now 的文章，自动触发发布
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledPublishService {

    private final ArticleRepository articleRepository;
    private final PlatformAccountRepository platformAccountRepository;
    private final PublishService publishService;

    /**
     * 每60秒执行一次，检查是否有需要定时发布的文章
     */
    @Scheduled(fixedDelay = 60_000)
    public void processScheduledPublishes() {
        try {
            List<Article> scheduledArticles = articleRepository.findByScheduledAtBeforeAndStatus(
                LocalDateTime.now(), "scheduled"
            );
            if (scheduledArticles.isEmpty()) return;

            log.info("发现 {} 篇定时发布文章，开始处理...", scheduledArticles.size());
            for (Article article : scheduledArticles) {
                try {
                    Long userId = article.getUser().getId();
                    List<PlatformAccount> accounts = platformAccountRepository.findByUserId(userId);
                    List<String> platforms = accounts.stream()
                            .map(PlatformAccount::getPlatform)
                            .toList();
                    if (!platforms.isEmpty()) {
                        log.info("定时发布文章[{}] 到平台: {}", article.getId(), platforms);
                        publishService.publishToMultiplePlatforms(article.getId(), userId, platforms);
                    }
                    // 更新状态（publishToMultiplePlatforms 内也会更新，这里作为兜底）
                    article.setScheduledAt(null);
                    articleRepository.save(article);
                } catch (Exception e) {
                    log.error("定时发布文章[{}]失败: {}", article.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("定时发布任务异常", e);
        }
    }
}
