package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class BaijiahaoPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://baijiahao.baidu.com/builder/rc/edit";
    }

    @Override
    protected String getCookieDomain() {
        return ".baidu.com";
    }

    @Override
    public String getPlatformName() {
        return "百家号";
    }
}
