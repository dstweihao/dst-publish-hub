package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class WeiboPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://weibo.com";
    }

    @Override
    protected String getCookieDomain() {
        return ".weibo.com";
    }

    @Override
    public String getPlatformName() {
        return "微博";
    }
}
