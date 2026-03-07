package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class CsdnPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://mp.csdn.net/mp_blog/creation/editor";
    }

    @Override
    protected String getCookieDomain() {
        return ".csdn.net";
    }

    @Override
    public String getPlatformName() {
        return "CSDN";
    }
}
