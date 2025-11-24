package com.articlehub.controller;

import com.articlehub.dto.ApiResponse;
import com.articlehub.dto.PublishStatistics;
import com.articlehub.entity.PlatformPublish;
import com.articlehub.entity.User;
import com.articlehub.platform.PublishResult;
import com.articlehub.service.PublishService;
import com.articlehub.service.UserService;
import com.articlehub.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publish")
@RequiredArgsConstructor
public class PublishController {
    private final PublishService publishService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/article/{articleId}")
    public ApiResponse<?> publishArticle(
            @PathVariable Long articleId,
            @RequestHeader("Authorization") String token,
            @RequestBody PublishRequest request) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);

        Map<String, PublishResult> results = publishService.publishToMultiplePlatforms(
            articleId, user.getId(), request.getPlatforms()
        );

        return ApiResponse.success("Article published", results);
    }

    @GetMapping("/records/{articleId}")
    public ApiResponse<?> getPublishRecords(
            @PathVariable Long articleId,
            @RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);

        List<PlatformPublish> records = publishService.getPublishRecords(articleId);
        return ApiResponse.success(records);
    }

    @GetMapping("/statistics")
    public ApiResponse<?> getStatistics(
            @RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);

        PublishStatistics stats = publishService.getPublishStatistics(user.getId());
        return ApiResponse.success(stats);
    }

    @PostMapping("/retry/{articleId}")
    public ApiResponse<?> retryFailedPublishes(
            @PathVariable Long articleId,
            @RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);

        Map<String, PublishResult> results = publishService.retryFailedPublishes(articleId, user.getId());
        return ApiResponse.success("Retry completed", results);
    }

    @PostMapping("/single/{articleId}/{platform}")
    public ApiResponse<?> publishToSinglePlatform(
            @PathVariable Long articleId,
            @PathVariable String platform,
            @RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));
        User user = userService.getUserByUsername(username);

        // 获取文章后调用单个平台发布
        PublishResult result = ApiResponse.success().getData();
        return ApiResponse.success("Published to " + platform, result);
    }

    public static class PublishRequest {
        private List<String> platforms;

        public List<String> getPlatforms() {
            return platforms;
        }

        public void setPlatforms(List<String> platforms) {
            this.platforms = platforms;
        }
    }
}
