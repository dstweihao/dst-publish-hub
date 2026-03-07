package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class DevtoPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://dev.to/new";
    }

    @Override
    protected String getCookieDomain() {
        return ".dev.to";
    }

    @Override
    public String getPlatformName() {
        return "Dev.to";
    }
}
