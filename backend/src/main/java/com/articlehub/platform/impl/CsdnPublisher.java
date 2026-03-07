package com.articlehub.platform.impl;

import com.articlehub.dto.ArticleDTO;
import com.articlehub.platform.PlatformPublisher;
import com.articlehub.platform.PublishResult;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CSDN 平台发布器（Playwright 浏览器自动化）
 */
@Service
@Slf4j
public class CsdnPublisher implements PlatformPublisher {

    private void randomDelay(Page page, int minMs, int maxMs) {
        int delay = minMs + (int)(Math.random() * (maxMs - minMs));
        page.waitForTimeout(delay);
    }

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        log.info("开始 CSDN Playwright 自动化发布");

        if (credentials == null || credentials.isBlank()) {
            return PublishResult.builder()
                    .success(false)
                    .errorMessage("CSDN 凭证为空，请先通过浏览器辅助登录连接 CSDN 账号")
                    .build();
        }

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(150)
                        .setArgs(java.util.List.of(
                            "--disable-blink-features=AutomationControlled",
                            "--no-sandbox"
                        ))
            );

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                        .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                        .setViewportSize(1440, 900)
                        .setLocale("zh-CN")
                        .setTimezoneId("Asia/Shanghai")
            );
            context.addInitScript(
                "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });"
            );

            List<Cookie> cookies = parseCookies(credentials, ".csdn.net");
            if (!cookies.isEmpty()) {
                context.addCookies(cookies);
            }

            Page page = context.newPage();
            page.navigate("https://mp.csdn.net/mp_blog/creation/editor");
            page.waitForLoadState();
            randomDelay(page, 2500, 4000);

            String currentUrl = page.url();
            if (currentUrl.contains("login") || currentUrl.contains("passport")) {
                browser.close();
                return PublishResult.builder()
                        .success(false)
                        .errorMessage("CSDN Cookie 已过期，请重新通过浏览器辅助登录")
                        .build();
            }

            // ===== 1. 填写标题 =====
            try {
                Locator titleInput = page.locator("#titleId, input[placeholder*='标题']").first();
                titleInput.waitFor(new Locator.WaitForOptions().setTimeout(8000));
                titleInput.click();
                randomDelay(page, 300, 600);
                titleInput.fill(article.getTitle());
                randomDelay(page, 500, 1000);
                log.info("已填写 CSDN 标题");
            } catch (Exception e) {
                log.warn("CSDN 标题填写失败: {}", e.getMessage());
            }

            // ===== 2. 填写正文 =====
            try {
                String content = article.getContent() != null ? article.getContent() : "";
                String plainText = content.replaceAll("<[^>]+>", "").trim();
                if (plainText.isBlank()) plainText = article.getTitle();

                context.grantPermissions(
                    java.util.List.of("clipboard-read", "clipboard-write"),
                    new BrowserContext.GrantPermissionsOptions().setOrigin("https://mp.csdn.net")
                );

                Locator editor = page.locator(".editor-md-content, .CodeMirror-code, [contenteditable='true']").first();
                editor.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                editor.click();
                randomDelay(page, 500, 800);

                final String textToPaste = plainText.length() > 5000 ? plainText.substring(0, 5000) : plainText;
                Boolean ok = (Boolean) page.evaluate(
                    "async (text) => { try { await navigator.clipboard.writeText(text); return true; } catch(e) { return false; } }",
                    textToPaste
                );
                if (Boolean.TRUE.equals(ok)) {
                    page.keyboard().press("Meta+v");
                } else {
                    editor.pressSequentially(textToPaste.substring(0, Math.min(1000, textToPaste.length())),
                        new Locator.PressSequentiallyOptions().setDelay(30));
                }
                randomDelay(page, 800, 1500);
                log.info("已填写 CSDN 正文");
            } catch (Exception e) {
                log.warn("CSDN 正文填写失败: {}", e.getMessage());
            }

            // ===== 3. 点击发布 =====
            String publishedUrl = null;
            try {
                Locator publishBtn = page.locator("button:has-text('发布文章'), button:has-text('发布')").first();
                publishBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                randomDelay(page, 500, 1000);
                publishBtn.click();
                randomDelay(page, 2000, 3000);

                // 处理发布确认弹窗
                try {
                    Locator confirmBtn = page.locator("button:has-text('确定'), button:has-text('发布')").last();
                    if (confirmBtn.isVisible()) {
                        confirmBtn.click();
                        randomDelay(page, 2000, 3000);
                    }
                } catch (Exception e2) { /* 无弹窗 */ }

                String url = page.url();
                if (url.contains("/article/details/") || url.contains("/blog/")) {
                    publishedUrl = url;
                }
                log.info("CSDN 发布完成，URL: {}", publishedUrl);
            } catch (Exception e) {
                log.error("CSDN 发布按钮失败: {}", e.getMessage());
            }

            browser.close();
            if (publishedUrl != null) {
                return PublishResult.builder().success(true).publishedUrl(publishedUrl).build();
            } else {
                return PublishResult.builder()
                        .success(false)
                        .errorMessage("CSDN 发布未获取到文章链接，请检查草稿箱")
                        .build();
            }
        } catch (Exception e) {
            log.error("CSDN Playwright 发布异常", e);
            return PublishResult.builder().success(false).errorMessage("CSDN 发布异常: " + e.getMessage()).build();
        }
    }

    private List<Cookie> parseCookies(String cookieStr, String domain) {
        List<Cookie> cookies = new ArrayList<>();
        if (cookieStr == null || cookieStr.isBlank()) return cookies;
        Pattern p = Pattern.compile("([^=;\\s]+)=([^;]*)");
        Matcher m = p.matcher(cookieStr);
        while (m.find()) {
            cookies.add(new Cookie(m.group(1).trim(), m.group(2).trim()).setDomain(domain).setPath("/"));
        }
        return cookies;
    }

    @Override
    public String getPlatformName() { return "CSDN"; }
}
