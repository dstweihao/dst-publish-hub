package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class HashnodePublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://hashnode.com/draft";
    }

    @Override
    protected String getCookieDomain() {
        return ".hashnode.com";
    }

    @Override
    public String getPlatformName() {
        return "Hashnode";
    }
}
