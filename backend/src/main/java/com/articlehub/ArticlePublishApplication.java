package com.articlehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = {"com.articlehub"})
public class ArticlePublishApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticlePublishApplication.class, args);
    }
}
