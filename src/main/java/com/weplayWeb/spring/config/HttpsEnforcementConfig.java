package com.weplayWeb.spring.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class HttpsEnforcementConfig {

	 private Environment env;
	 
	 public HttpsEnforcementConfig(Environment env) {
	        this.env = env;
	    }
	 
	 
 
	  @Bean
	    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        
		  if (Arrays.asList(env.getActiveProfiles()).contains("local") 
			|| Arrays.asList(env.getActiveProfiles()).contains("test")) {
	            http.requiresChannel(channel -> channel.anyRequest().requiresInsecure());
	        } else {
	        	  http.requiresChannel(channel ->channel.anyRequest().requiresSecure());
	        }
	
	        http.authorizeHttpRequests(authorize ->authorize
	        		.requestMatchers(new AntPathRequestMatcher("/**", HttpMethod.OPTIONS.name())).permitAll()
	        		.anyRequest().permitAll());
	        return http.build();
	    }
}