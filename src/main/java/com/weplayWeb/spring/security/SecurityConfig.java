package com.weplayWeb.spring.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;

import org.springframework.core.env.Environment;
import com.weplayWeb.spring.config.CorsConfig;

import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@Order(1)
public class SecurityConfig {
    
    private final Environment env;
    private final CorsConfig corsConfig;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    public SecurityConfig(Environment env, CorsConfig corsConfig) {
        this.env = env;
        this.corsConfig = corsConfig;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        
        logger.info("Configuring security for profile: {}", String.join(", ", env.getActiveProfiles()));
        
        http
            .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .frameOptions(frame -> frame.deny())
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; connect-src 'self' https://weplayofficial.com https://www.weplayofficial.com"))
            )
            .requiresChannel(channel -> {
                if (env.acceptsProfiles(profiles -> profiles.test("prod"))) {
                    channel.anyRequest().requiresSecure();
                }
            })
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(mvcMatcherBuilder.pattern("api/payment/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("api/customer/**")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/")).permitAll()      
                .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico")).permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
    
    @Bean
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }
}