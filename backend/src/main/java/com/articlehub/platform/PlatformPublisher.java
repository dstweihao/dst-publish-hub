package com.articlehub.platform;

import com.articlehub.dto.ArticleDTO;

import java.util.Map;

public interface PlatformPublisher {
    /**
     * 发布文章到平台
     */
    PublishResult publish(String accountId, String credentials, ArticleDTO article, Map<String, Object> extraMeta);

    /**
     * 获取平台名称
     */
    String getPlatformName();

    /**
     * 刷新 Cookie/Token 凭据（后台定时任务保活用）
     * 默认不执行任何操作，直接返回原凭据
     */
    default String refreshCookie(String credentials) {
        return credentials;
    }
}
