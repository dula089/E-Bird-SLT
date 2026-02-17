package com.E_Bird_Telecome.E_Bird_React_Backend.Config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "organizations", 
            "employees", 
            "costCenters",
            "requests",      // Add this
            "request"        // Add this for single request cache
        );
    }
}