package com.articlehub.repository;

import com.articlehub.entity.Article;
import com.articlehub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepositoryExtension extends JpaRepository<Article, Long> {

    List<Article> findByUserUsername(String username);

    List<Article> findByCategory(String category);

    List<Article> findByStatus(String status);

    Page<Article> findByUser(User user, Pageable pageable);

    long countByUser(User user);

    long countByUserAndStatus(User user, String status);
}