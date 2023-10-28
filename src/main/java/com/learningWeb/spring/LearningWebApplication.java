package com.learningWeb.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;

@SpringBootApplication
public class LearningWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningWebApplication.class, args);
	}

//	@Bean
//	 WebMvcConfigurer corsConfig(){
//		
//		return new WebMvcConfigurer() {
//			
//			   @Override
//			    public void addCorsMappings(CorsRegistry registry) {
//
//			        registry
//			        		.addMapping("/**")
//			                .allowedOrigins("http://localhost:4200/") 
//			                .allowedMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PUT, HttpMethod.OPTIONS)
//			                .allowedHeaders(HttpHeaders.CONTENT_TYPE, HttpHeaders.AUTHORIZATION);
//			             
//			    
//			}
//			
//		};		
//	}	 
}
