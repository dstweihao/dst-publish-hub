package com.articlehub.platform.impl;

import java.util.Map;
import com.articlehub.dto.ArticleDTO;
import com.articlehub.platform.PlatformPublisher;
import com.articlehub.platform.PublishResult;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 知乎平台发布实现 - 基于 Playwright 浏览器自动化
 */
@Service
@Slf4j
public class ZhihuPublisher implements PlatformPublisher {

    // 随机延迟 ms
    private void randomDelay(Page page, int minMs, int maxMs) {
        int delay = minMs + (int)(Math.random() * (maxMs - minMs));
        page.waitForTimeout(delay);
    }

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article, Map<String, Object> extraMeta) {
        log.info("开始知乎 Playwright 自动化发布 - 账号: {}", accountId);

        if (credentials == null || credentials.isBlank()) {
            return PublishResult.builder()
                    .success(false)
                    .errorMessage("知乎凭证为空，请先通过浏览器辅助登录连接知乎账号")
                    .build();
        }

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(150)
                        // 关键：禁用自动化控制特征标识
                        .setArgs(java.util.List.of(
                            "--disable-blink-features=AutomationControlled",
                            "--no-sandbox",
                            "--disable-web-security"
                        ))
            );

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                        .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                        .setViewportSize(1440, 900)
                        .setLocale("zh-CN")
                        .setTimezoneId("Asia/Shanghai")
            );

            // 在每个页面加载前注入反检测脚本，隐藏 navigator.webdriver
            context.addInitScript(
                "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });"
                + "Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] });"
                + "Object.defineProperty(navigator, 'languages', { get: () => ['zh-CN', 'zh', 'en'] });"
                + "window.chrome = { runtime: {} };"
            );

            // 授予剪贴板权限（指定 origin 才生效）
            context.grantPermissions(
                java.util.List.of("clipboard-read", "clipboard-write"),
                new BrowserContext.GrantPermissionsOptions().setOrigin("https://zhuanlan.zhihu.com")
            );

            List<Cookie> cookies = parseCookies(credentials, ".zhihu.com");
            if (!cookies.isEmpty()) {
                context.addCookies(cookies);
                log.info("已注入 {} 个知乎 Cookie", cookies.size());
            }

            Page page = context.newPage();

            // 先访问知乎首页让 Cookie 和反检测脚本生效
            log.info("导航至知乎首页...");
            page.navigate("https://www.zhihu.com");
            page.waitForLoadState();
            randomDelay(page, 2000, 3500);

            log.info("导航至知乎专栏创作页...");
            page.navigate("https://zhuanlan.zhihu.com/write");
            page.waitForLoadState();
            randomDelay(page, 2000, 3000);

            // 检查是否被重定向到登录页
            String currentUrl = page.url();
            log.info("当前页面 URL: {}", currentUrl);
            if (currentUrl.contains("signin") || currentUrl.contains("login")) {
                browser.close();
                return PublishResult.builder()
                        .success(false)
                        .errorMessage("知乎 Cookie 已过期，请重新通过浏览器辅助登录")
                        .build();
            }

            // 关闭创作助手弹窗
            try {
                Locator closeBtn = page.locator("[class*='CreatorAside'] button, .Modal-closeButton").first();
                if (closeBtn.isVisible()) {
                    closeBtn.click();
                    randomDelay(page, 500, 800);
                }
            } catch (Exception e) { /* 弹窗不存在 */ }
            page.keyboard().press("Escape");
            randomDelay(page, 300, 600);

            // ===== 1. 填写标题 =====
            try {
                page.waitForSelector("textarea[placeholder*='标题'], div[placeholder*='标题']",
                        new Page.WaitForSelectorOptions().setTimeout(10000));
                Locator titleInput = page.locator("textarea[placeholder*='标题'], div[placeholder*='标题']").first();
                titleInput.click();
                randomDelay(page, 400, 800);
                titleInput.pressSequentially(article.getTitle(), new Locator.PressSequentiallyOptions().setDelay(40));
                log.info("已填写标题: {}", article.getTitle());
                randomDelay(page, 600, 1200);
            } catch (Exception e) {
                log.warn("标题填写失败: {}", e.getMessage());
            }

            // ===== 2. 填写正文（剪贴板粘贴富文本，解决图片等 Markdown 渲染问题）=====
            try {
                String content = article.getContent() != null ? article.getContent() : "";
                
                // 1. 将 Markdown 转换为 HTML
                Parser parser = Parser.builder().build();
                Node document = parser.parse(content);
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                String htmlContent = renderer.render(document);

                // 2. 纯文本降级备份
                String plainText = content.replaceAll("<[^>]+>", "").replaceAll("&nbsp;", " ").trim();
                if (plainText.isBlank()) {
                    plainText = article.getTitle() + "\n\n（请在知乎草稿中补充正文内容）";
                }
                if (plainText.length() > 5000) {
                    plainText = plainText.substring(0, 5000) + "...\n（内容较长，请查看草稿）";
                }

                // 点击正文区域
                randomDelay(page, 500, 1000);
                Locator contentArea = page.locator("[contenteditable='true']").last();
                contentArea.click();
                randomDelay(page, 600, 1000);

                // 3. 将 HTML 和纯文本同时写入剪贴板，使用 ClipboardItem，由于页面在 TLS 下，需要提前赋予权限
                Boolean clipboardOk = (Boolean) page.evaluate(
                    "async ([text, html]) => {\n" +
                    "    try {\n" +
                    "        const htmlBlob = new Blob([html], { type: 'text/html' });\n" +
                    "        const textBlob = new Blob([text], { type: 'text/plain' });\n" +
                    "        const item = new ClipboardItem({ 'text/html': htmlBlob, 'text/plain': textBlob });\n" +
                    "        await navigator.clipboard.write([item]);\n" +
                    "        return true;\n" +
                    "    } catch(e) {\n" +
                    "        console.error('Clipboard write failed:', e);\n" +
                    "        return false;\n" +
                    "    }\n" +
                    "}",
                    java.util.Arrays.asList(plainText, htmlContent)
                );
                log.info("富文本剪贴板写入状态: {}", clipboardOk);

                if (Boolean.TRUE.equals(clipboardOk)) {
                    // Mac 使用 Meta+V，Windows/Linux 使用 Control+V
                    page.keyboard().press("Meta+v");
                    randomDelay(page, 500, 800);
                    // 检查是否粘贴成功（字数变化）
                    Object charCount = page.evaluate("() => document.querySelector('[contenteditable]') ? document.querySelector('[contenteditable]').innerText.length : 0");
                    log.info("粘贴后编辑器字数: {}", charCount);
                    if (charCount == null || ((Number)charCount).intValue() < 5) {
                        // 尝试 Control+V
                        page.keyboard().press("Control+v");
                        randomDelay(page, 500, 800);
                    }
                    log.info("已通过剪贴板粘贴富文本正文");
                } else {
                    // 回退：逐字输入（限制 1000 字避免太慢）
                    log.warn("剪贴板写入失败，回退逐字输入");
                    contentArea.pressSequentially(plainText.substring(0, Math.min(1000, plainText.length())), new Locator.PressSequentiallyOptions().setDelay(30));
                }
                randomDelay(page, 800, 1500);
            } catch (Exception e) {
                log.warn("正文输入失败: {}", e.getMessage());
            }


            page.waitForTimeout(1000);

            // ===== 3. 点击右上角"发布"按钮，进入发布设置页 =====
            String publishedUrl = null;
            try {
                // 找到并点击编辑器右上角的"发布"按钮
                Locator publishBtn = page.locator("button.Button.WriteIndex-publishBtn, button:has-text('发布')").last();
                publishBtn.waitFor(new Locator.WaitForOptions().setTimeout(8000));
                publishBtn.click();
                log.info("已点击编辑器发布按钮，等待进入发布设置页...");

                // 等待进入发布设置页（URL 变化 或 出现发布设置的特征元素）
                page.waitForTimeout(2000);
                log.info("发布后当前 URL: {}", page.url());

                // ===== 4. 在发布设置页自动填写话题 =====
                try {
                    autoAddTopics(page, article);
                } catch (Exception topicEx) {
                    log.warn("自动添加话题失败（不影响发布）: {}", topicEx.getMessage());
                }

                // ===== 5. 点击发布设置页的最终"发布"按钮 =====
                page.waitForTimeout(500);
                try {
                    // 发布设置页底部右下角的"发布"按钮
                    Locator finalPublishBtn = page.locator(
                        "button.Button--primary:has-text('发布'), " +
                        ".PublishPanel button:has-text('发布'), " +
                        "button.Button.Button--blue:has-text('发布')"
                    ).last();
                    finalPublishBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
                    finalPublishBtn.click();
                    log.info("已点击发布设置页最终发布按钮");
                } catch (Exception e2) {
                    // 备用：扫描所有按钮找"发布"
                    log.info("精准选择器未找到，尝试全页扫描...");
                    page.evaluate(
                        "() => {" +
                        "  const btns = [...document.querySelectorAll('button')];" +
                        "  const pub = btns.filter(b => b.innerText.trim() === '发布');" +
                        "  if (pub.length > 0) pub[pub.length - 1].click();" +
                        "}"
                    );
                }

                // 等待发布完成、跳转到文章页
                try {
                    page.waitForURL("**/p/**", new Page.WaitForURLOptions().setTimeout(20000));
                    publishedUrl = page.url();
                    log.info("知乎发布成功！URL: {}", publishedUrl);
                } catch (Exception ex) {
                    String nowUrl = page.url();
                    log.warn("URL 未跳转到文章页，当前: {}", nowUrl);
                    if (nowUrl.contains("/p/")) {
                        publishedUrl = nowUrl;
                    }
                }
            } catch (Exception e) {
                log.error("发布流程失败: {}", e.getMessage());
            }

            browser.close();

            if (publishedUrl != null) {
                return PublishResult.builder()
                        .success(true)
                        .publishedUrl(publishedUrl)
                        .build();
            } else {
                return PublishResult.builder()
                        .success(false)
                        .errorMessage("发布流程执行完毕，但未获取到文章链接。请检查知乎草稿箱，文章可能已保存为草稿待手动发布。")
                        .build();
            }

        } catch (Exception e) {
            log.error("知乎 Playwright 发布异常", e);
            return PublishResult.builder()
                    .success(false)
                    .errorMessage("知乎发布异常: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 在知乎发布设置页自动添加话题
     * 从文章标题提取关键词，搜索并选择相关话题（最多3个）
     */
    private void autoAddTopics(Page page, ArticleDTO article) throws Exception {
        log.info("开始自动添加知乎话题...");

        // 从标题中提取关键词（按空格/标点分词，取长度 >= 2 的词）
        String title = article.getTitle() != null ? article.getTitle() : "";
        String[] words = title.split("[\\s,，。.！!？?、；;：:]+");
        java.util.List<String> keywords = new java.util.ArrayList<>();
        for (String w : words) {
            if (w.length() >= 2) {
                keywords.add(w);
            }
        }
        // 如果标题词不够，加入整个标题作为一个关键词
        if (keywords.isEmpty()) {
            keywords.add(title);
        }
        // 最多尝试添加3个话题
        if (keywords.size() > 3) {
            keywords = keywords.subList(0, 3);
        }
        log.info("话题关键词: {}", keywords);

        int addedCount = 0;
        for (String keyword : keywords) {
            if (addedCount >= 3) break;
            try {
                // 点击"添加话题"按钮
                Locator addTopicBtn = page.locator(
                    "button:has-text('添加话题'), [class*='TopicEditor'] button, span:has-text('添加话题')"
                ).first();
                if (!addTopicBtn.isVisible()) {
                    log.info("添加话题按钮不可见，跳过");
                    break;
                }
                addTopicBtn.click();
                page.waitForTimeout(500);

                // 等待话题搜索输入框出现
                Locator searchInput = page.locator(
                    "input[placeholder*='搜索话题'], input[placeholder*='话题'], [class*='TopicSearch'] input"
                ).first();
                searchInput.waitFor(new Locator.WaitForOptions().setTimeout(3000));
                searchInput.fill("");
                searchInput.type(keyword);
                page.waitForTimeout(1000); // 等待搜索结果

                // 选择第一个搜索结果
                Locator firstResult = page.locator(
                    "[class*='TopicItem']:first-child, [class*='topic-item']:first-child, [class*='SelectMenu-item']:first-child"
                ).first();
                firstResult.waitFor(new Locator.WaitForOptions().setTimeout(2000));
                firstResult.click();
                page.waitForTimeout(300);
                addedCount++;
                log.info("已添加话题: {}", keyword);
            } catch (Exception e) {
                log.info("话题 '{}' 添加失败，跳过: {}", keyword, e.getMessage());
            }
        }
        log.info("共添加 {} 个话题", addedCount);
    }

    private List<Cookie> parseCookies(String cookieStr, String domain) {
        List<Cookie> cookies = new ArrayList<>();
        if (cookieStr == null || cookieStr.isBlank()) return cookies;

        Pattern p = Pattern.compile("([^=;\\s]+)=([^;]*)");
        Matcher m = p.matcher(cookieStr);
        while (m.find()) {
            String name = m.group(1).trim();
            String value = m.group(2).trim();
            if (!name.isEmpty()) {
                Cookie cookie = new Cookie(name, value);
                cookie.domain = domain;
                cookie.path = "/";
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    @Override
    public String getPlatformName() {
        return "知乎";
    }
}
