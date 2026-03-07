package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class DoubanPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://www.douban.com/note/create";
    }

    @Override
    protected String getCookieDomain() {
        return ".douban.com";
    }

    @Override
    public String getPlatformName() {
        return "豆瓣";
    }
}
