package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class SegmentfaultPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://segmentfault.com/write";
    }

    @Override
    protected String getCookieDomain() {
        return ".segmentfault.com";
    }

    @Override
    public String getPlatformName() {
        return "思否";
    }
}
