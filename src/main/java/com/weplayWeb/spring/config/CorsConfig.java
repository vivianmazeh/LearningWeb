package com.weplayWeb.spring.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import com.weplayWeb.spring.Square.CreateCustomer;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebMvc
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed.origin}")
    private String[] corsAllowedOrigins;
    
    private static final Logger logger = LoggerFactory.getLogger(CreateCustomer.class);
    
    
    @Bean
	public CorsConfigurationSource corsConfigurationSource() {
    	
        CorsConfiguration config = new CorsConfiguration();
        
        for(String origin: corsAllowedOrigins) {
        	logger.info("CORS Allowed Origin: " + origin.trim());
        	 config.addAllowedOrigin(origin.trim()); 
        }
    		
        
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList(
        		   "Origin",
                   "Content-Type",
                   "Accept",
                   "Authorization",
                   "X-Requested-With",
                   "Access-Control-Request-Method",
                   "Access-Control-Request-Headers",
                   "X-Forwarded-Proto",
                   "X-Forwarded-For",
                   "X-Real-IP"
                
            ));
        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Location"
            ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow all methods
        config.setMaxAge(3600L); // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(corsAllowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}