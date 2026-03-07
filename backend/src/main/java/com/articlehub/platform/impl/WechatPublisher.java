package com.articlehub.platform.impl;

import org.springframework.stereotype.Service;

@Service
public class WechatPublisher extends AbstractPlaywrightPublisher {

    @Override
    protected String getEditorUrl() {
        return "https://mp.weixin.qq.com/";
    }

    @Override
    protected String getCookieDomain() {
        return ".qq.com";
    }

    @Override
    public String getPlatformName() {
        return "微信公众号";
    }
}
