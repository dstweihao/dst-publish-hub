package com.articlehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishStatistics {
    private long totalArticles;
    private long publishedArticles;
    private long draftArticles;
    private long archivedArticles;
    private long totalViews;
    private long totalLikes;
    private long totalComments;
    private long totalPlatforms;
    private long activePlatforms;
    private long totalPublishes;
}