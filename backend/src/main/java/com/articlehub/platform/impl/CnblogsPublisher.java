package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class CnblogsPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://i.cnblogs.com/posts/edit";
    }

    @Override
    protected String getCookieDomain() {
        return ".cnblogs.com";
    }

    @Override
    public String getPlatformName() {
        return "博客园";
    }
}
