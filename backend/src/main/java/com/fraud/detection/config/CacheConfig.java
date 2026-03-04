package com.fraud.detection.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine for high-performance in-memory caching.
 * Significantly improves rule evaluation and feature computation performance.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Caffeine cache manager with optimized settings.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "rules",           // Active rules cache
                "userFeatures",    // User transaction features
                "ruleExpressions"  // Compiled SpEL expressions
        );
        
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)                      // Max 1000 entries per cache
                .expireAfterWrite(5, TimeUnit.MINUTES)  // Expire after 5 minutes
                .recordStats());                         // Enable metrics
        
        return cacheManager;
    }

    /**
     * Separate cache for frequently accessed users (longer TTL).
     */
    @Bean
    public Caffeine<Object, Object> userCacheConfiguration() {
        return Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .recordStats();
    }
}
