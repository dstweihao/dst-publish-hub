package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class MediumPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://medium.com/new-story";
    }

    @Override
    protected String getCookieDomain() {
        return ".medium.com";
    }

    @Override
    public String getPlatformName() {
        return "Medium";
    }
}
