package com.weplayWeb.spring.Square;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CustomersApi;
import com.squareup.square.models.CreateCustomerRequest;
import com.squareup.square.models.CreateCustomerResponse;




@Service
public class CreateCustomer {
	

	@Autowired
	private SquareClient squareClient;
	
	public CustomerResponse customerResponse;
	
	private static final Logger logger = LoggerFactory.getLogger(CreateCustomer.class);
	
	 public CreateCustomer() { }
	
	
	 public ResponseEntity<CustomerResponse> createCustomerResponse(TokenWrapper tokenObject) throws IOException{
		 
		 logger.debug("Received token object: {}", tokenObject);
	        
	        if (tokenObject == null) {
	            logger.error("TokenWrapper is null");
	            return ResponseEntity.badRequest().body(new CustomerResponse(null, "TokenWrapper is null"));
	        }

	        if (tokenObject.getSourceId() == null) {
	            logger.error("Token is null");
	            return ResponseEntity.badRequest().body(new CustomerResponse(null, "Token source id is null"));
	        }

	        if (tokenObject.getCustomer() == null) {
	            logger.error("CustomerInfo is null");
	            return ResponseEntity.badRequest().body(new CustomerResponse(null, "CustomerInfo is null"));
	        }

	        CustomersApi customerApi= squareClient.getCustomersApi();
	 
	            // Create customer request object
	            CreateCustomerRequest customerRequest = new CreateCustomerRequest.Builder()
	                .idempotencyKey(UUID.randomUUID().toString())
	                .givenName(tokenObject.getCustomer().getGivenName())
	                .familyName(tokenObject.getCustomer().getFamilyName())
	                .emailAddress(tokenObject.getCustomer().getEmailAddress())
	                .phoneNumber(tokenObject.getCustomer().getPhoneNumber())
	                .referenceId(tokenObject.getSourceId())
	                .build();
	            
	            logger.debug("Sending request to Square API: {}", customerRequest);

	            // Call Square API to create customer
	   
	            
	            CreateCustomerResponse result;
				try {
					result = customerApi.createCustomerAsync(customerRequest).get();
					
					if (result.getCustomer() != null) {
                        String customerId = result.getCustomer().getId();
                        logger.info("Customer created successfully with ID: {}", customerId);
                        return ResponseEntity.ok(new CustomerResponse(customerId, "Customer created successfully"));
                    } else {
                        logger.error("Failed to create customer - no customer returned from Square API");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new CustomerResponse(null, "Failed to create customer"));
                    }
				} catch (InterruptedException e) {
					 logger.error("Operation interrupted: ", e);
			            Thread.currentThread().interrupt(); // Restore the interrupted status
			            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			                .body(new CustomerResponse(null, "Operation interrupted during customer creatation"));
				} catch (ExecutionException e) {
		            logger.error("Execution error: ", e);
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body(new CustomerResponse(null, "Execution error: " + e.getMessage()));
		        }
	 }

}
