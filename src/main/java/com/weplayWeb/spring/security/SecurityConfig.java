package com.weplayWeb.spring.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import com.weplayWeb.spring.config.CorsConfig;

import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfig corsConfig, HandlerMappingIntrospector introspector) throws Exception {
    	
    	MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
    	
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF
            .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource())) // Apply global CORS configuration
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(mvcMatcherBuilder.pattern("/payment")).permitAll() // Specify MvcRequestMatcher for Spring MVC endpoint
                .requestMatchers(mvcMatcherBuilder.pattern("/customer")).permitAll()
                .anyRequest().authenticated()); // Secure other endpoints

        return http.build();
    }

    // Add this bean to handle Spring MVC patterns
    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
}
