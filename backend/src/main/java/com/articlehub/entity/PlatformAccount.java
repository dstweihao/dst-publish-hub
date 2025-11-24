package com.articlehub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "platform_accounts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "platform"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String platform; // wechat, juejin, cnblogs, zhihu, csdn, toutiao, jianshu

    @Column(nullable = false)
    private String accountId; // 平台账号ID

    @Column(columnDefinition = "TEXT")
    private String credentials; // 加密存储的认证信息

    private String accountName; // 平台显示的账号名

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
