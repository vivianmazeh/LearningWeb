package com.weplayWeb.spring.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed.origin}")
    private String corsAllowedOrigin;
    
    @PostConstruct
    public void init() {
        System.out.println("CORS Allowed Origin: " + corsAllowedOrigin);
    }
    
    @Bean
	public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(corsAllowedOrigin));  // Allow specific origin
        config.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // Allow all methods

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}