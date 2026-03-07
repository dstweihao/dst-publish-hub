package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class XiaohongshuPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://creator.xiaohongshu.com/publish/publish?source=official";
    }

    @Override
    protected String getCookieDomain() {
        return ".xiaohongshu.com";
    }

    @Override
    public String getPlatformName() {
        return "小红书";
    }
}
