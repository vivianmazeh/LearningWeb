package com.learningWeb.spring.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class HttpsEnforcementConfig {

	 private Environment env;
	 
	 public HttpsEnforcementConfig(Environment env) {
	        this.env = env;
	    }
	 
	 
 
	  @Bean
	    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        
		  if (Arrays.asList(env.getActiveProfiles()).contains("local")) {
	            http.requiresChannel(channel -> channel.anyRequest().requiresInsecure());
	        } else {
	        	  http.requiresChannel(channel ->channel.anyRequest().requiresSecure());
	        }
	
	        http.authorizeHttpRequests(authorize ->authorize.anyRequest().permitAll());
	        return http.build();
	    }
}