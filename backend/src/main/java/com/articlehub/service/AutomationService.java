package com.articlehub.service;

import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自动化登录助手服务
 * 利用 Playwright 引导用户在真实浏览器中登录并截获凭证
 */
@Service
@Slf4j
public class AutomationService {

    public Map<String, String> captureCredentials(String platform) {
        log.info("准备启动浏览器辅助登录助手 - 目标平台: {}", platform);
        
        try (Playwright playwright = Playwright.create()) {
            log.debug("Playwright 运行时环境初始化成功");
            
            Browser browser;
            try {
                // 启动可见模式的 Chromium
                browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(50));
                log.debug("Chromium 浏览器窗口启动成功 (可见模式)");
            } catch (Exception e) {
                log.error("浏览器启动失败！这通常是因为缺少驱动或权限不足。请确保已执行控制台安装命令。原因: {}", e.getMessage());
                throw new RuntimeException("浏览器启动失败，请检查环境: " + e.getMessage());
            }
            
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            
            AtomicReference<Map<String, String>> credentials = new AtomicReference<>(new HashMap<>());
            String loginUrl = getLoginUrl(platform);
            
            log.info("正在导航至登录页面: {}", loginUrl);
            page.navigate(loginUrl);
            
            boolean captured = false;
            int timeoutSeconds = 300; // 5分钟超时
            long startTime = System.currentTimeMillis();
            
            while (!captured && (System.currentTimeMillis() - startTime) < timeoutSeconds * 1000) {
                if (page.isClosed()) {
                    log.warn("用户主动关闭了浏览器窗口，放弃凭证捕获");
                    break;
                }
                
                var cookies = context.cookies();
                captured = validateLoginSuccess(platform, cookies, credentials);
                
                if (!captured) {
                    Thread.sleep(2000); // 没两秒检查一次
                }
            }
            
            if (captured) {
                log.info("恭喜！截获跨站凭证成功，正在自动关闭浏览器...");
            } else {
                log.warn("在超时时间内未检测到登录成功的 Cookie 标识");
            }
            
            browser.close();
            return credentials.get();
        } catch (Exception e) {
            log.error("自动化登录链路异常: ", e);
            throw new RuntimeException("自动化登录失败: " + e.getMessage());
        }
    }

    private String getLoginUrl(String platform) {
        return switch (platform) {
            case "知乎" -> "https://www.zhihu.com/signin";
            case "稀土掘金" -> "https://juejin.cn/login";
            case "微信公众号" -> "https://mp.weixin.qq.com/";
            case "CSDN" -> "https://passport.csdn.net/login";
            default -> throw new IllegalArgumentException("暂不支持该平台的自动化登录: " + platform);
        };
    }

    private boolean validateLoginSuccess(String platform, java.util.List<com.microsoft.playwright.options.Cookie> cookies, AtomicReference<Map<String, String>> result) {
        Map<String, String> data = new HashMap<>();
        
        StringBuilder sb = new StringBuilder();
        boolean hasZhihuToken = false;
        boolean hasJuejinToken = false;
        
        for (var cookie : cookies) {
            sb.append(cookie.name).append("=").append(cookie.value).append("; ");
            if (cookie.name.equals("z_c0")) hasZhihuToken = true;
            if (cookie.name.equals("sessionid")) hasJuejinToken = true;
        }
        String allCookies = sb.toString();

        switch (platform) {
            case "知乎" -> {
                if (hasZhihuToken) {
                    data.put("token", allCookies);
                    data.put("username", "知乎账号 (已自动截获)");
                    result.set(data);
                    log.info("Successfully captured Zhihu credentials");
                    return true;
                }
            }
            case "稀土掘金" -> {
                if (hasJuejinToken) {
                    data.put("token", allCookies);
                    data.put("username", "掘金账号 (已自动截获)");
                    result.set(data);
                    log.info("Successfully captured Juejin credentials");
                    return true;
                }
            }
            default -> {
                // 通用逻辑：对于其他平台，如果 Cookie 数量显著增长（通常 10 个以上），认为已登录
                if (cookies.size() > 10) {
                    data.put("token", allCookies);
                    data.put("username", platform + "账号 (已自动截获)");
                    result.set(data);
                    log.info("Successfully captured credentials for {}", platform);
                    return true;
                }
            }
        }
        return false;
    }
}
