package com.articlehub.controller;

import com.articlehub.entity.User;
import com.articlehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 会员套餐相关接口
 */
@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserRepository userRepository;

    /** 获取所有套餐信息（公开） */
    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        List<Map<String, Object>> plans = new ArrayList<>();

        Map<String, Object> free = new LinkedHashMap<>();
        free.put("id", "FREE"); free.put("name", "免费版"); free.put("price", 0); free.put("yearPrice", 0);
        free.put("articles", 5); free.put("platforms", 2); free.put("aiEnabled", false);
        free.put("scheduledPublish", false); free.put("analytics", false); free.put("teamMembers", 1);
        free.put("features", List.of("每月5篇文章", "2个平台", "基础发布功能"));
        plans.add(free);

        Map<String, Object> pro = new LinkedHashMap<>();
        pro.put("id", "PRO"); pro.put("name", "专业版"); pro.put("price", 29); pro.put("yearPrice", 199);
        pro.put("articles", -1); pro.put("platforms", -1); pro.put("aiEnabled", true);
        pro.put("scheduledPublish", true); pro.put("analytics", true); pro.put("teamMembers", 1);
        pro.put("features", List.of("无限文章", "全部平台", "AI 发布优化", "定时发布", "数据分析仪表板", "优先客服"));
        plans.add(pro);

        Map<String, Object> team = new LinkedHashMap<>();
        team.put("id", "TEAM"); team.put("name", "团队版"); team.put("price", 99); team.put("yearPrice", 799);
        team.put("articles", -1); team.put("platforms", -1); team.put("aiEnabled", true);
        team.put("scheduledPublish", true); team.put("analytics", true); team.put("teamMembers", 5);
        team.put("features", List.of("专业版全部功能", "5个子账号", "团队协作+内容审核", "批量文章迁移", "专属客服"));
        plans.add(team);

        return ResponseEntity.ok(plans);
    }

    /** 获取当前用户的套餐状态 */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        boolean isPro = "PRO".equals(user.getPlan()) || "TEAM".equals(user.getPlan());
        boolean isExpired = user.getPlanExpiredAt() != null && user.getPlanExpiredAt().isBefore(LocalDateTime.now());
        String effectivePlan = (isPro && !isExpired) ? user.getPlan() : "FREE";
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("plan", effectivePlan);
        result.put("rawPlan", user.getPlan());
        result.put("expiredAt", user.getPlanExpiredAt() != null ? user.getPlanExpiredAt().toString() : null);
        result.put("inviteCode", user.getInviteCode() != null ? user.getInviteCode() : "");
        result.put("isExpired", isExpired);
        return ResponseEntity.ok(result);
    }

    /** 生成我的邀请码 */
    @PostMapping("/generate-invite-code")
    public ResponseEntity<?> generateInviteCode(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (user.getInviteCode() == null || user.getInviteCode().isBlank()) {
            user.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            userRepository.save(user);
        }
        return ResponseEntity.ok(Map.of("inviteCode", user.getInviteCode()));
    }

    /** 使用邀请码激活 14 天 Pro */
    @PostMapping("/redeem-invite")
    public ResponseEntity<?> redeemInvite(@RequestBody Map<String, String> body, Authentication auth) {
        String code = body.get("inviteCode");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "邀请码不能为空"));
        }
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (currentUser.getInvitedByCode() != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "您已使用过邀请码"));
        }
        User inviter = userRepository.findByInviteCode(code).orElse(null);
        if (inviter == null) return ResponseEntity.badRequest().body(Map.of("error", "邀请码无效"));
        if (inviter.getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body(Map.of("error", "不能使用自己的邀请码"));
        }

        LocalDateTime bonus = LocalDateTime.now().plusDays(14);
        LocalDateTime inviterExpiry = (inviter.getPlanExpiredAt() != null && inviter.getPlanExpiredAt().isAfter(LocalDateTime.now()))
                ? inviter.getPlanExpiredAt().plusDays(14) : bonus;
        inviter.setPlan("PRO"); inviter.setPlanExpiredAt(inviterExpiry);
        userRepository.save(inviter);

        currentUser.setPlan("PRO"); currentUser.setPlanExpiredAt(bonus);
        currentUser.setInvitedByCode(code);
        userRepository.save(currentUser);

        return ResponseEntity.ok(Map.of("message", "邀请码激活成功！您和邀请人均获得14天专业版"));
    }
}
