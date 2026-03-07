package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class JianshuPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://www.jianshu.com/writer#/";
    }

    @Override
    protected String getCookieDomain() {
        return ".jianshu.com";
    }

    @Override
    public String getPlatformName() {
        return "简书";
    }
}
