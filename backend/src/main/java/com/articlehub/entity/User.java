package com.articlehub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    /** 会员套餐：FREE | PRO | TEAM */
    @Builder.Default
    private String plan = "FREE";

    /** 套餐到期时间，null 表示永久或免费 */
    private LocalDateTime planExpiredAt;

    /** 此用户的邀请码（唯一） */
    private String inviteCode;

    /** 被谁邀请注册的邀请码 */
    private String invitedByCode;

    private String role;
    private String avatar;

    @Column(nullable = false)
    private boolean enabled = true;

    private String bio;

    @Builder.Default
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}