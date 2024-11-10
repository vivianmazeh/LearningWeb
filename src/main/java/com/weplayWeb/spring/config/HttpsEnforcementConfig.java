package com.weplayWeb.spring.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@Order(2)
public class HttpsEnforcementConfig {

    private final Environment env;
    private static final Logger logger = LoggerFactory.getLogger(HttpsEnforcementConfig.class);

    public HttpsEnforcementConfig(Environment env) {
        this.env = env;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring HTTPS enforcement for profile: " + Arrays.toString(env.getActiveProfiles()));

        // Configure based on environment
        if (Arrays.asList(env.getActiveProfiles()).contains("local")
                || Arrays.asList(env.getActiveProfiles()).contains("test")) {
            http.requiresChannel(channel -> channel.anyRequest().requiresInsecure());
        } else {
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        }

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
            );

        return http.build();
    }
}