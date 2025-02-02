package com.weplayWeb.spring.config;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebMvc
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed.origin}")
    private String[] corsAllowedOrigins;
    
    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);
    

    public List<String> addValidOrigins(){
    	 // Process and validate origins
        List<String> validOrigins = new ArrayList<>();
        for (String origin : corsAllowedOrigins) {
            String trimmedOrigin = origin.trim();
            if (!trimmedOrigin.isEmpty()) {
                // Add both www and non-www versions if it's our domain
                if (trimmedOrigin.contains("weplayofficial.com")) {
                    String wwwOrigin = "https://www.weplayofficial.com";
                    String nonWwwOrigin = "https://weplayofficial.com";
                    
                    if (!validOrigins.contains(wwwOrigin)) {
                        validOrigins.add(wwwOrigin);
                        logger.info("Added www origin: {}", wwwOrigin);
                    }
                    if (!validOrigins.contains(nonWwwOrigin)) {
                        validOrigins.add(nonWwwOrigin);
                        logger.info("Added non-www origin: {}", nonWwwOrigin);
                    }
                } else {
                    // For other domains, add as-is
                    validOrigins.add(trimmedOrigin);
                    logger.info("Added origin: {}", trimmedOrigin);
                }
            }
        }
        
        return validOrigins;    
       
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Log initial configuration
        logger.info("Initializing CORS configuration with provided origins: {}", 
            Arrays.toString(corsAllowedOrigins));

       

        // Add all valid origins to configuration
        addValidOrigins().forEach(config::addAllowedOrigin);
        
        // Configure headers
        config.setAllowedHeaders(Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization",
            "X-Requested-With", "Access-Control-Request-Method",
            "Access-Control-Request-Headers", "X-Forwarded-Proto",
            "X-Forwarded-For", "CF-Connecting-IP", "X-Real-IP",
            "Cache-Control", "Pragma", "X-CSP-Nonce"
        ));
        
        // Configure exposed headers
        config.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Headers",
            "Access-Control-Allow-Methods",
            "Access-Control-Max-Age",
            "Location",
            "Content-Disposition",
            "X-Total-Count",
            "X-CSP-Nonce"
        ));
        // Configure essential CORS settings
        config.setAllowCredentials(true);
       
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
            ));
        
    
        config.setMaxAge(3600L); // 1 hour

 
        // Create and configure the source
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        source.registerCorsConfiguration("/api/**", config);
        
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> bean = 
            new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        bean.addUrlPatterns("/*");
        return bean;
    }

}