package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class ToutiaoPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://mp.toutiao.com/profile_v4/graphic/publish";
    }

    @Override
    protected String getCookieDomain() {
        return ".toutiao.com";
    }

    @Override
    public String getPlatformName() {
        return "今日头条";
    }
}
