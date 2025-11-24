package com.articlehub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "platform_publishes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"article_id", "platform"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformPublish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false)
    private String platform; // wechat, juejin, cnblogs, zhihu, csdn, toutiao, jianshu

    @Column(name = "status")
    private String status = "pending"; // pending, published, failed

    @Column(name = "published_url")
    private String publishedUrl;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_account_id")
    private PlatformAccount platformAccount;
}
