package com.E_Bird_Telecome.E_Bird_React_Backend.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport
public class PaginationConfig {
    // Remove the CacheManager bean from here
    // This class should ONLY handle pagination configuration
}