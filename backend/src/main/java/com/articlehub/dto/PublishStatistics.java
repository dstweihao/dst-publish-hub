package com.articlehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishStatistics {
    private long totalArticles;
    private long publishedArticles;
    private long totalPublishes;
    private float successRate;
}
