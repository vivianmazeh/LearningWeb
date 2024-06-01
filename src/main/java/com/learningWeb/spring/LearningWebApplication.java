package com.learningWeb.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.learningWeb.spring.polulationData.GetCityProfiles;

@SpringBootApplication
public class LearningWebApplication  {
		
	public static void main(String[] args) {
		SpringApplication.run(LearningWebApplication.class, args);
	}


	// @Bean:applied on a method to specify that it returns a bean to be managed by Spring context
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
