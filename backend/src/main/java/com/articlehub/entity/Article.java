package com.articlehub.repository;

import com.articlehub.entity.Article;
import com.articlehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepositoryExtension extends JpaRepository<Article, Long> {
    long countByUser(User user);
    long countByUserAndStatus(User user, String status);
}
