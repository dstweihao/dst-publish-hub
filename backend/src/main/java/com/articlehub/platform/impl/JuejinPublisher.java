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
 * 掘金平台发布器（Playwright 浏览器自动化）
 */
@Service
@Slf4j
public class JuejinPublisher implements PlatformPublisher {

    private void randomDelay(Page page, int minMs, int maxMs) {
        int delay = minMs + (int)(Math.random() * (maxMs - minMs));
        page.waitForTimeout(delay);
    }

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article) {
        log.info("开始掘金 Playwright 自动化发布 - 账号: {}", accountId);

        if (credentials == null || credentials.isBlank()) {
            return PublishResult.builder()
                    .success(false)
                    .errorMessage("掘金凭证为空，请先通过浏览器辅助登录连接掘金账号")
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
                + "window.chrome = { runtime: {} };"
            );

            // 注入 Cookie
            List<Cookie> cookies = parseCookies(credentials, ".juejin.cn");
            if (!cookies.isEmpty()) {
                context.addCookies(cookies);
                log.info("已注入 {} 个掘金 Cookie", cookies.size());
            }

            Page page = context.newPage();

            // 先访问首页让 Cookie 生效
            page.navigate("https://juejin.cn");
            page.waitForLoadState();
            randomDelay(page, 1500, 3000);

            // 导航至掘金创作页面
            log.info("导航至掘金创作页...");
            page.navigate("https://juejin.cn/editor/drafts/new?v=2");
            page.waitForLoadState();
            randomDelay(page, 2000, 3000);

            // 检查登录状态
            String currentUrl = page.url();
            log.info("当前 URL: {}", currentUrl);
            if (currentUrl.contains("login") || currentUrl.contains("signin")) {
                browser.close();
                return PublishResult.builder()
                        .success(false)
                        .errorMessage("掘金 Cookie 已过期，请重新通过浏览器辅助登录")
                        .build();
            }

            // ===== 1. 填写标题 =====
            try {
                Locator titleInput = page.locator(".title-input, input[placeholder*='标题'], [placeholder='输入文章标题...']").first();
                titleInput.waitFor(new Locator.WaitForOptions().setTimeout(8000));
                titleInput.click();
                randomDelay(page, 300, 600);
                titleInput.fill(article.getTitle());
                log.info("已填写掘金标题: {}", article.getTitle());
                randomDelay(page, 500, 1000);
            } catch (Exception e) {
                log.warn("掘金标题填写失败: {}", e.getMessage());
            }

            // ===== 2. 填写正文（掘金使用 Markdown 编辑器）=====
            try {
                String content = article.getContent() != null ? article.getContent() : "";
                String plainText = content.replaceAll("<[^>]+>", "").replaceAll("&nbsp;", " ").trim();
                if (plainText.isBlank()) plainText = article.getTitle();

                // 掘金 Markdown 编辑器的 contenteditable
                Locator editor = page.locator(".bytemd-editor .CodeMirror-code, [contenteditable='true']").first();
                editor.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                editor.click();
                randomDelay(page, 500, 800);

                // 使用剪贴板粘贴
                context.grantPermissions(
                    java.util.List.of("clipboard-read", "clipboard-write"),
                    new BrowserContext.GrantPermissionsOptions().setOrigin("https://juejin.cn")
                );
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
                log.info("已填写掘金正文");
            } catch (Exception e) {
                log.warn("掘金正文填写失败: {}", e.getMessage());
            }

            // ===== 3. 点击发布按钮 =====
            String publishedUrl = null;
            try {
                // 掘金的发布按钮
                Locator publishBtn = page.locator("button:has-text('发布文章'), button:has-text('发布')").first();
                publishBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                randomDelay(page, 500, 1000);
                publishBtn.click();
                log.info("已点击掘金发布按钮");
                randomDelay(page, 2000, 3000);

                // 处理发布弹窗（掘金有分类选择弹窗）
                try {
                    // 确认发布
                    Locator confirmBtn = page.locator("button:has-text('确定发布'), button:has-text('立即发布')").first();
                    if (confirmBtn.isVisible()) {
                        confirmBtn.click();
                        randomDelay(page, 2000, 3000);
                    }
                } catch (Exception e2) { /* 没有确认弹窗 */ }

                // 获取发布后的 URL
                try {
                    page.waitForURL("**/post/**", new Page.WaitForURLOptions().setTimeout(15000));
                    publishedUrl = page.url();
                } catch (Exception e3) {
                    String url = page.url();
                    if (url.contains("/post/")) publishedUrl = url;
                }
                log.info("掘金发布完成，URL: {}", publishedUrl);
            } catch (Exception e) {
                log.error("掘金发布按钮操作失败: {}", e.getMessage());
            }

            browser.close();

            if (publishedUrl != null) {
                return PublishResult.builder().success(true).publishedUrl(publishedUrl).build();
            } else {
                return PublishResult.builder()
                        .success(false)
                        .errorMessage("掘金发布未能获取到文章链接，文章可能已保存为草稿")
                        .build();
            }
        } catch (Exception e) {
            log.error("掘金 Playwright 发布异常", e);
            return PublishResult.builder().success(false).errorMessage("掘金发布异常: " + e.getMessage()).build();
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
    public String getPlatformName() {
        return "稀土掘金";
    }
}
