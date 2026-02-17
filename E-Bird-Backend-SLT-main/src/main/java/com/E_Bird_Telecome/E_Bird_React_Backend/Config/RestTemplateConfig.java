package com.E_Bird_Telecome.E_Bird_React_Backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Set timeouts (adjust based on your needs)
        factory.setConnectTimeout(5000); // 5 seconds
        factory.setReadTimeout(10000);    // 10 seconds
        
        // Enable buffer for large responses
        factory.setBufferRequestBody(false);
        
        return new RestTemplate(factory);
    }
}