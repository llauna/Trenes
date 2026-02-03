package com.david.trenes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public SecurityFilterChain securityHeadersFilter(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                // Frame Protection
                .frameOptions(frameOptions -> frameOptions
                    .deny()
                )
                
                // Content Type Options
                .contentTypeOptions(contentTypeOptions -> {})
                
                // Referrer Policy
                .referrerPolicy(referrerPolicy -> referrerPolicy
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
                
                // Cache Control for security
                .cacheControl(cacheControl -> {})
            );
        
        return http.build();
    }
}
