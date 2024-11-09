package com.weplayWeb.spring.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import com.weplayWeb.spring.config.CorsConfig;

import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final CorsFilter corsFilter;
	
	  public SecurityConfig(CorsFilter corsFilter) {
	        this.corsFilter = corsFilter;
	    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfig corsConfig, HandlerMappingIntrospector introspector) throws Exception {
    	
    	MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
    	
        http   
        	.addFilterBefore(corsFilter, ChannelProcessingFilter.class)
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
