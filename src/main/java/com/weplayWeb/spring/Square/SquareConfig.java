package com.weplayWeb.spring.Square;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.authentication.BearerAuthModel;

@Configuration
public class SquareConfig {
	
	 @Value("${square.accessToken}")
	    private String accessToken;

	 @Value("${square.environment}")
	    private String environment;

	  // Initialize SquareClient after all fields have been injected
	    @Bean
	    SquareClient squareClient() {
		        return new SquareClient.Builder()
		                .environment(environment.equalsIgnoreCase("sandbox") ? Environment.SANDBOX : Environment.PRODUCTION)
		                .bearerAuthCredentials(new BearerAuthModel.Builder(accessToken).build())
		                .build();
		    }
}
