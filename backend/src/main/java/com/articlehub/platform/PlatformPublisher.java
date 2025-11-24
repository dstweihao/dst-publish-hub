package com.articlehub.platform;

import com.articlehub.dto.ArticleDTO;

public interface PlatformPublisher {
    /**
     * 发布文章到平台
     */
    PublishResult publish(String accountId, String credentials, ArticleDTO article);

    /**
     * 获取平台名称
     */
    String getPlatformName();
}
