package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class OpenSourceChinaPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://my.oschina.net/u/xxx/blog/write";
    }

    @Override
    protected String getCookieDomain() {
        return ".oschina.net";
    }

    @Override
    public String getPlatformName() {
        return "开源中国";
    }
}
