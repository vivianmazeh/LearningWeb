package com.weplayWeb.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WePlayWebApplication  {
		
	public static void main(String[] args) {
		SpringApplication.run(WePlayWebApplication.class, args);
	} 
}
