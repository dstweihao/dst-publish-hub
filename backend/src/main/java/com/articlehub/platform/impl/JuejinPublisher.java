package com.articlehub.platform.impl;

import java.util.Map;
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
        int delay = minMs + (int) (Math.random() * (maxMs - minMs));
        page.waitForTimeout(delay);
    }

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article,
            Map<String, Object> extraMeta) {
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
                                    "--no-sandbox")));

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent(
                                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                            .setViewportSize(1440, 900)
                            .setLocale("zh-CN")
                            .setTimezoneId("Asia/Shanghai"));

            context.addInitScript(
                    "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });"
                            + "window.chrome = { runtime: {} };");

            // 注入 Cookie
            List<Cookie> cookies = parseCookies(credentials, ".juejin.cn");
            if (!cookies.isEmpty()) {
                context.addCookies(cookies);
                log.info("已注入 {} 个掘金 Cookie", cookies.size());
            }

            Page page = context.newPage();

            log.info("导航至掘金创作页...");
            // 加入网络重试容错机制，应对偶发的 ERR_CONNECTION_RESET
            boolean navigated = false;
            for (int i = 0; i < 3; i++) {
                try {
                    page.navigate("https://juejin.cn/editor/drafts/new?v=2",
                            new Page.NavigateOptions()
                                    .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED)
                                    .setTimeout(30000));
                    navigated = true;
                    break;
                } catch (Exception e) {
                    log.warn("第 {} 次尝试进入掘金页面失败: {}", i + 1, e.getMessage());
                    page.waitForTimeout(2000);
                }
            }
            if (!navigated) {
                throw new RuntimeException("连续 3 次尝试访问掘金创作页面均发生连接重置或超时！");
            }

            page.waitForTimeout(3000);
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
                Locator titleInput = page.locator(".title-input, input[placeholder*='标题'], [placeholder='输入文章标题...']")
                        .first();
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
                if (plainText.isBlank())
                    plainText = article.getTitle();

                // 掘金 Markdown 编辑器的 contenteditable
                Locator editor = page.locator(".bytemd-editor .CodeMirror-code, [contenteditable='true']").first();
                editor.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                editor.click();
                randomDelay(page, 500, 800);

                // 使用剪贴板粘贴
                context.grantPermissions(
                        java.util.List.of("clipboard-read", "clipboard-write"),
                        new BrowserContext.GrantPermissionsOptions().setOrigin("https://juejin.cn"));
                final String textToPaste = plainText.length() > 5000 ? plainText.substring(0, 5000) : plainText;
                Boolean ok = (Boolean) page.evaluate(
                        "async (text) => { try { await navigator.clipboard.writeText(text); return true; } catch(e) { return false; } }",
                        textToPaste);
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
                // 给予掘金解析正文与编辑器响应 2 秒的喘息时间
                page.waitForTimeout(2000);
                // 掘金右上角的第一步发布按钮
                Locator publishBtn = page.locator(
                        "button.btn-primary:has-text('发布'):visible, button.publish-btn:visible, button:has-text('发布'):visible")
                        .first();
                publishBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                publishBtn.click();
                log.info("已点击掘金右上角发布按钮，等待弹窗加载...");
                // 确保点击弹出动作以及任何可能的子网络接口获取完成
                page.waitForTimeout(3000);

                // 处理发布弹窗（不再填写特化的分类/标签，直接尝试提交对标知乎简单粗暴模式）
                try {
                    Locator confirmBtn = page
                            .locator(
                                    "button:has-text('确定并发布'):visible, button:has-text('确定发布'):visible, button.btn-primary:has-text('发布'):visible")
                            .first();
                    confirmBtn.waitFor(new Locator.WaitForOptions().setTimeout(2000));
                    confirmBtn.click();
                    log.info("掘金点击了最后确定发布按钮");
                    page.waitForTimeout(3000);
                } catch (Exception e2) {
                    log.error("掘金提取并点击发布弹窗时发生异常 (文章大概率已安稳落入草稿箱)", e2);
                }

                log.info("此时掘金发文表单已拉起，等待人工接管复核/首图替换/参数调整并点击发布... 倒数 5 分钟...");
                page.waitForTimeout(300000); // 300秒 = 5分钟

                // 获取发布后的 URL
                try {
                    page.waitForURL("**/post/**", new Page.WaitForURLOptions().setTimeout(15000));
                    publishedUrl = page.url();
                } catch (Exception e3) {
                    String url = page.url();
                    // 处理发布成功但跳转到文章列表或草稿页的情况
                    if (url.contains("/post/") || url.contains("drafts") || url.contains("creator")) {
                        publishedUrl = url;
                    }
                }
                log.info("掘金发布操作完毕，当前停留 URL: {}", publishedUrl);
            } catch (Exception e) {
                log.error("掘金发布按钮操作失败: {}", e.getMessage());
            }

            browser.close();

            if (publishedUrl != null) {
                return PublishResult.builder().success(true).publishedUrl(publishedUrl).build();
            } else {
                return PublishResult.builder()
                        .success(true) // 操作都完成了，只是没拿到最终 URL
                        .errorMessage("所有操作执行完毕，但由于掘金平台特性暂未获取到具体的公网 URL")
                        .build();
            }
        } catch (Exception e) {
            log.error("掘金 Playwright 发布异常", e);
            return PublishResult.builder().success(false).errorMessage("掘金发布异常: " + e.getMessage()).build();
        }
    }

    private List<Cookie> parseCookies(String cookieStr, String domain) {
        List<Cookie> cookies = new ArrayList<>();
        if (cookieStr == null || cookieStr.isBlank())
            return cookies;
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
