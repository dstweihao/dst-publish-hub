package com.articlehub.controller;

import com.articlehub.dto.ApiResponse;
import com.articlehub.service.AutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 自动化功能控制器
 */
@RestController
@RequestMapping("/automation")
@RequiredArgsConstructor
public class AutomationController {

    private final AutomationService automationService;

    /**
     * 触发浏览器辅助登录
     * @param platform 平台名称
     */
    @PostMapping("/login-assist")
    public ApiResponse<Map<String, String>> startLoginAssist(@RequestParam String platform) {
        try {
            Map<String, String> credentials = automationService.captureCredentials(platform);
            if (credentials.isEmpty()) {
                return ApiResponse.error("未能截获到登录凭证，请确保已成功登录并重试");
            }
            return ApiResponse.success("凭证获取成功", credentials);
        } catch (Exception e) {
            return ApiResponse.error("自动化登录失败: " + e.getMessage());
        }
    }
}
