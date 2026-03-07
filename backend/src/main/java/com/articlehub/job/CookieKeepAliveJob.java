package com.articlehub.job;

import com.articlehub.entity.PlatformAccount;
import com.articlehub.platform.PlatformPublisher;
import com.articlehub.platform.PlatformPublisherFactory;
import com.articlehub.repository.PlatformAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台 Cookie 全自动保活与续期打卡引擎
 */
@Component
@Slf4j
public class CookieKeepAliveJob {

    @Autowired
    private PlatformAccountRepository platformAccountRepository;

    @Autowired
    private PlatformPublisherFactory platformPublisherFactory;

    /**
     * 每天凌晨03:00和下午15:00执行一次
     * "0 0 3,15 * * ?"
     * （为了方便测试，我们这里先配一个每天四次："0 0 0/6 * * ?"，即每6小时一次）
     */
    @Scheduled(cron = "0 0 0/6 * * ?")
    public void executeKeepAlive() {
        log.info("========== 开始执行平台凭证 (Cookie) 自动续命保活作业 ==========");
        List<PlatformAccount> activeAccounts = platformAccountRepository.findByIsActiveTrueAndCredentialsIsNotNull();
        if (activeAccounts.isEmpty()) {
            log.info("当前没有检测到任何已绑定的有效平台凭证，保活作业结束。");
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (PlatformAccount account : activeAccounts) {
            String platform = account.getPlatform();
            log.info("正在为账号所属平台执行保活打卡探测: {}", platform);

            if (!platformPublisherFactory.isSupported(platform)) {
                log.warn("跳过不支持的平台: {}", platform);
                continue;
            }

            try {
                PlatformPublisher publisher = platformPublisherFactory.getPublisher(platform);
                String oldCredentials = account.getCredentials();
                // 调用刚引入的挂靠底层刷新功能
                String newCredentials = publisher.refreshCookie(oldCredentials);

                if (newCredentials != null && !newCredentials.isBlank() && !newCredentials.equals(oldCredentials)) {
                    log.info("成功捕获到平台 {} 下发的新凭证，正在覆盖落库...", platform);
                    account.setCredentials(newCredentials);
                    account.setUpdatedAt(LocalDateTime.now());
                    platformAccountRepository.save(account);
                    successCount++;
                } else if (newCredentials != null && newCredentials.equals(oldCredentials)) {
                    log.info("平台 {} 未下发新凭证或依然有效，原纪录保留不覆盖。", platform);
                    successCount++;
                } else {
                    log.warn("警告：平台 {} 保活后返回了空凭证，可能已被网站拒绝服务。", platform);
                    failCount++;
                }
            } catch (Exception e) {
                log.error("在为 {} 平台账号保活时遇到异常: {}", platform, e.getMessage());
                failCount++;
            }
        }

        log.info("========== 平台自动续命保活作业完成。成功/续命 {} 个，异常 {} 个。==========", successCount, failCount);
    }
}
