package com.articlehub.repository;

import com.articlehub.entity.PlatformAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformAccountRepository extends JpaRepository<PlatformAccount, Long> {
    Optional<PlatformAccount> findByUserIdAndPlatform(Long userId, String platform);
    List<PlatformAccount> findByUserId(Long userId);
    List<PlatformAccount> findByUserIdAndIsActive(Long userId, Boolean isActive);
}
