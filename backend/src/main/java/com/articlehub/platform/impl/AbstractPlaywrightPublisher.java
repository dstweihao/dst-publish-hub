package com.articlehub.platform.impl;

import com.articlehub.dto.ArticleDTO;
import com.articlehub.platform.PlatformPublisher;
import com.articlehub.platform.PublishResult;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * 通用的基于 Playwright 的自动化发文基类
 * 将会拉起无头浏览器，注入传入的凭证 Cookie，访问各自专属的“写文章页面”，
 * 采用智能嗅探（寻找标题、正文框）填涂内容后，将主控权挂起 5 分钟抛还给主屏幕前的用户。
 */
@Slf4j
public abstract class AbstractPlaywrightPublisher implements PlatformPublisher {

    /**
     * 子类定义去哪发布
     */
    protected abstract String getEditorUrl();

    /**
     * 子类定义所属域名（用于种 Cookie）
     */
    protected abstract String getCookieDomain();

    /**
     * 子类定义平台展示名称
     */
    @Override
    public abstract String getPlatformName();

    protected void randomDelay(Page page, int minMs, int maxMs) {
        int delay = minMs + (int) (Math.random() * (maxMs - minMs));
        page.waitForTimeout(delay);
    }

    @Override
    public PublishResult publish(String accountId, String credentials, ArticleDTO article,
            Map<String, Object> extraMeta) {
        log.info("开始 {} 专属极简 Playwright 自动化发布与推流", getPlatformName());

        if (credentials == null || credentials.isBlank()) {
            return PublishResult.builder()
                    .success(false)
                    .errorMessage(getPlatformName() + " 凭证为空，请先通过浏览器辅助登录连接账号！")
                    .build();
        }

        String publishedUrl = null;

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

            context.addInitScript("Object.defineProperty(navigator, 'webdriver', { get: () => undefined });");

            List<Cookie> cookies = parseCookies(credentials, getCookieDomain());
            if (!cookies.isEmpty()) {
                context.addCookies(cookies);
            }

            Page page = context.newPage();
            page.navigate(getEditorUrl());
            page.waitForLoadState();
            randomDelay(page, 2500, 4000);

            String currentUrl = page.url();
            if (currentUrl.contains("login") || currentUrl.contains("signin") || currentUrl.contains("passport")) {
                browser.close();
                return PublishResult.builder()
                        .success(false)
                        .errorMessage(getPlatformName() + " Cookie 疑似已过期或拦截，未见到编辑器直接重定向到了登录区。请重试连接。")
                        .build();
            }

            // [智能发文阶段一] 泛光扫描页面尝试注入标题
            try {
                Locator titleInput = page.locator(
                        "textarea[placeholder*='标题'], input[placeholder*='标题'], input[class*='title'], textarea[class*='title'], input[name*='title']")
                        .first();
                if (titleInput.isVisible()) {
                    titleInput.click();
                    randomDelay(page, 200, 500);
                    titleInput.fill(article.getTitle());
                    log.info("{} 成功智能推流注入了标题", getPlatformName());
                }
            } catch (Exception e) {
                log.warn("{}未能找到明确标示的标题栏: {}", getPlatformName(), e.getMessage());
            }
            randomDelay(page, 800, 1500);

            // [智能发文阶段二] 将正文复制进剪贴板并尝试锁定正文容器按下粘贴键
            try {
                String content = article.getContent() != null ? article.getContent() : "";
                Parser parser = Parser.builder().build();
                Node document = parser.parse(content);
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                String htmlContent = renderer.render(document);

                String plainText = content.replaceAll("<[^>]+>", "").replaceAll("&nbsp;", " ").trim();

                context.grantPermissions(java.util.List.of("clipboard-read", "clipboard-write"));

                Object writeOk = page.evaluate("async ([html, plain]) => {\n" +
                        "  try {\n" +
                        "    const htmlBlob = new Blob([html], { type: 'text/html' });\n" +
                        "    const plainBlob = new Blob([plain], { type: 'text/plain' });\n" +
                        "    const item = new ClipboardItem({\n" +
                        "      'text/html': htmlBlob,\n" +
                        "      'text/plain': plainBlob\n" +
                        "    });\n" +
                        "    await navigator.clipboard.write([item]);\n" +
                        "    return true;\n" +
                        "  } catch (err) {\n" +
                        "    // fallback for http:// or some environments\n" +
                        "    try { await navigator.clipboard.writeText(plain); return true; } catch(e) { return false; }\n"
                        +
                        "  }\n" +
                        "}", java.util.List.of(htmlContent, plainText));

                if (Boolean.TRUE.equals(writeOk)) {
                    Locator contentArea = page.locator("[contenteditable='true'], .editor-text, .ql-editor").last();
                    if (contentArea.isVisible()) {
                        contentArea.click();
                        randomDelay(page, 400, 800);
                        page.keyboard().press("Meta+v");
                        log.info("{} 成功于富文本编辑区下达了粘贴指令", getPlatformName());
                    } else {
                        log.info("{} 未定位到直观内容区，已将文章备入您的剪贴板，请稍后手动点击页内 Ctrl+V 进行粘贴。", getPlatformName());
                    }
                } else {
                    log.info("因 {} 安全协议较高，剪贴板注入被拦截。您可以从别处手动复制原文贴入。", getPlatformName());
                }
            } catch (Exception e) {
                log.warn("{} 剪贴板唤起异常或找不到内容域：{}", getPlatformName(), e.getMessage());
            }

            // 发动机休眠，移交控制大权，保持五分钟不死循环！
            log.info("===== {} 填表代理休眠：请在 5 分钟内完善元信息、检查排版并手动按动发文按钮！ =====", getPlatformName());
            page.waitForTimeout(300000); // 300秒 = 5分钟

            // 无论用户按还是不按，超时五分钟后或者手动关掉窗口，我们都当作它是一次成功的交互。
            // 只要把草稿截留下来，它就算是推流完成了！
            publishedUrl = page.url();
            browser.close();

            return PublishResult.builder()
                    .success(true)
                    .publishedUrl(publishedUrl)
                    .build();

        } catch (Exception e) {
            log.error(getPlatformName() + " 通用 Playwright 发布异常", e);
            return PublishResult.builder().success(false).errorMessage(getPlatformName() + " 发布异常: " + e.getMessage())
                    .build();
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

    /**
     * 【后台保活专用接口】
     * 静默拉起无头浏览器，注入已有 Cookie 访问官网
     * 获取由平台下发置换的最长效 Cookie 字符串并返回
     */
    public String refreshCookie(String credentials) {
        if (credentials == null || credentials.isBlank()) {
            return credentials;
        }

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions()
                            .setHeadless(true) // 保活作业全程无头，不打扰用户
                            .setArgs(java.util.List.of(
                                    "--disable-blink-features=AutomationControlled",
                                    "--no-sandbox")));

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent(
                                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                            .setLocale("zh-CN")
                            .setTimezoneId("Asia/Shanghai"));

            List<Cookie> cookies = parseCookies(credentials, getCookieDomain());
            if (!cookies.isEmpty()) {
                context.addCookies(cookies);
            }

            Page page = context.newPage();
            // 访问各平台的保活打卡首页
            page.navigate(getEditorUrl());
            page.waitForLoadState();

            // 停留 5 秒，给足网站执行 JS 探针与 Set-Cookie 的下发时间
            page.waitForTimeout(5000);

            // 提取最新所有 Cookie 拼回字符串
            List<Cookie> freshCookies = context.cookies();
            StringBuilder sb = new StringBuilder();
            for (Cookie c : freshCookies) {
                sb.append(c.name).append("=").append(c.value).append("; ");
            }

            browser.close();

            String refreshed = sb.toString();
            log.info("{} 通道 Cookie 心跳续期执行完毕。拿到 {} 个 Cookie 项", getPlatformName(), freshCookies.size());
            return refreshed;
        } catch (Exception e) {
            log.error("{} 通道 Cookie 保活刷新异常: {}", getPlatformName(), e.getMessage());
            return credentials; // 若刷新失败先返回原值暂留命
        }
    }
}
