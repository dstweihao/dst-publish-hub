package com.articlehub.repository;

import com.articlehub.entity.Article;
import com.articlehub.entity.PlatformPublish;
import com.articlehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatformPublishRepository extends JpaRepository<PlatformPublish, Long> {
    List<PlatformPublish> findByArticleId(Long articleId);
    List<PlatformPublish> findByArticleIdAndStatus(Long articleId, String status);

    @Query("SELECT COUNT(p) FROM PlatformPublish p WHERE p.article.user = :user AND p.status = :status")
    long countByArticleUserAndStatus(User user, String status);

    @Query("SELECT COUNT(p) FROM PlatformPublish p WHERE p.article.user = :user")
    long countByArticleUser(User user);
}
