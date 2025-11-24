package com.articlehub.repository;

import com.articlehub.entity.Article;
import com.articlehub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByUser(User user, Pageable pageable);
    Page<Article> findByUserAndStatus(User user, String status, Pageable pageable);
    Page<Article> findByUserAndTitleContaining(User user, String title, Pageable pageable);
}
