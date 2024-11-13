package com.weplayWeb.spring.Square;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CustomersApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreateCustomerRequest;
import com.squareup.square.models.CreateCustomerResponse;
import com.weplayWeb.spring.services.CSPService;




@Service
public class CreateCustomer {
	

	@Autowired
	private SquareClient squareClient;
	
	public CustomerResponse customerResponse;
	
	 @Autowired
	 private CSPService cspService;
	
	private static final Logger logger = LoggerFactory.getLogger(CreateCustomer.class);
	
	 public CreateCustomer() { }
	
	  public ResponseEntity<CustomerResponse> createCustomerResponse(TokenWrapper tokenObject) throws IOException {
	        logger.debug("Received token object: {}", tokenObject);
	        
	        // Input validation
	        if (!isValidRequest(tokenObject)) {
	            return handleInvalidRequest(tokenObject);
	        }
	        
	        try {
	            // Generate nonce for CSP
	            String nonce = cspService.generateNonce();
	            
	            // Create customer in Square
	            CustomersApi customerApi = squareClient.getCustomersApi();
	            CreateCustomerRequest customerRequest = buildCustomerRequest(tokenObject);
	            
	            logger.debug("Sending request to Square API: {}", customerRequest);
	            
	            CreateCustomerResponse result = customerApi.createCustomer(customerRequest);
	            
	            // Handle response
	            return buildResponse(result, nonce);
	            
	        } catch (ApiException e) {
	            return handleApiException(e);
	        } catch (Exception e) {
	            return handleGeneralException(e);
	        }
	    }
	
	  private boolean isValidRequest(TokenWrapper tokenObject) {
	        return tokenObject != null 
	            && tokenObject.getSourceId() != null 
	            && tokenObject.getCustomer() != null
	            && tokenObject.getCustomer().getGivenName() != null
	            && tokenObject.getCustomer().getFamilyName() != null;
	    }
	  
	  private ResponseEntity<CustomerResponse> handleInvalidRequest(TokenWrapper tokenObject) {
	        String errorMessage;
	        if (tokenObject == null) {
	            errorMessage = "TokenWrapper is null";
	        } else if (tokenObject.getSourceId() == null) {
	            errorMessage = "Token source id is null";
	        } else if (tokenObject.getCustomer() == null) {
	            errorMessage = "CustomerInfo is null";
	        } else {
	            errorMessage = "Invalid customer information";
	        }
	        logger.error(errorMessage);
	        return ResponseEntity.badRequest()
	            .body(new CustomerResponse(null, errorMessage));
	    }
	  
	  private CreateCustomerRequest buildCustomerRequest(TokenWrapper tokenObject) {
	        return new CreateCustomerRequest.Builder()
	            .idempotencyKey(UUID.randomUUID().toString())
	            .givenName(tokenObject.getCustomer().getGivenName())
	            .familyName(tokenObject.getCustomer().getFamilyName())
	            .emailAddress(tokenObject.getCustomer().getEmailAddress())
	            .phoneNumber(tokenObject.getCustomer().getPhoneNumber())
	            .referenceId(tokenObject.getSourceId())
	            .build();
	    }
	    
	    private ResponseEntity<CustomerResponse> buildResponse(CreateCustomerResponse result, String nonce) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Content-Security-Policy", cspService.generateCSPHeader(nonce));
	        headers.set("X-CSP-Nonce", nonce);
	        
	        if (result.getCustomer() != null) {
	            String customerId = result.getCustomer().getId();
	            logger.info("Customer created successfully with ID: {}", customerId);
	            return ResponseEntity.ok()
	                .headers(headers)
	                .body(new CustomerResponse(customerId, "Customer created successfully"));
	        } else {
	            logger.error("Failed to create customer - no customer returned from Square API");
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .headers(headers)
	                .body(new CustomerResponse(null, "Failed to create customer"));
	        }
	    }
	    
	    private ResponseEntity<CustomerResponse> handleApiException(ApiException e) {
	        logger.error("Square API Exception: ", e);
	        String errorMessage = String.format("Square API error: %s (Code: %s)", 
	            e.getMessage(), e.getErrors() != null ? e.getErrors().toString() : "unknown");
	        
	        HttpHeaders headers = new HttpHeaders();
	        String nonce = cspService.generateNonce();
	        headers.set("Content-Security-Policy", cspService.generateCSPHeader(nonce));
	        headers.set("X-CSP-Nonce", nonce);
	        
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .headers(headers)
	            .body(new CustomerResponse(null, errorMessage));
	    }
	    
	    private ResponseEntity<CustomerResponse> handleGeneralException(Exception e) {
	        logger.error("Unexpected error during customer creation: ", e);
	        
	        HttpHeaders headers = new HttpHeaders();
	        String nonce = cspService.generateNonce();
	        headers.set("Content-Security-Policy", cspService.generateCSPHeader(nonce));
	        headers.set("X-CSP-Nonce", nonce);
	        
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .headers(headers)
	            .body(new CustomerResponse(null, "An unexpected error occurred"));
	    }
//	 public ResponseEntity<CustomerResponse> createCustomerResponse(TokenWrapper tokenObject) throws IOException{
//		 
//		 logger.debug("Received token object: {}", tokenObject);
//	        
//	        if (tokenObject == null) {
//	            logger.error("TokenWrapper is null");
//	            return ResponseEntity.badRequest().body(new CustomerResponse(null, "TokenWrapper is null"));
//	        }
//
//	        if (tokenObject.getSourceId() == null) {
//	            logger.error("Token is null");
//	            return ResponseEntity.badRequest().body(new CustomerResponse(null, "Token source id is null"));
//	        }
//
//	        if (tokenObject.getCustomer() == null) {
//	            logger.error("CustomerInfo is null");
//	            return ResponseEntity.badRequest().body(new CustomerResponse(null, "CustomerInfo is null"));
//	        }
//
//	        String nonce = cspService.generateNonce();
//	        
//	        CustomersApi customerApi= squareClient.getCustomersApi();
//	 
//	            // Create customer request object
//	            CreateCustomerRequest customerRequest = new CreateCustomerRequest.Builder()
//	                .idempotencyKey(UUID.randomUUID().toString())
//	                .givenName(tokenObject.getCustomer().getGivenName())
//	                .familyName(tokenObject.getCustomer().getFamilyName())
//	                .emailAddress(tokenObject.getCustomer().getEmailAddress())
//	                .phoneNumber(tokenObject.getCustomer().getPhoneNumber())
//	                .referenceId(tokenObject.getSourceId())
//	                .build();
//	            
//	            logger.debug("Sending request to Square API: {}", customerRequest);
//
//	            // Call Square API to create customer
//	   
//	            
//	            CreateCustomerResponse result;
//				try {
//					
//					result =customerApi.createCustomer(customerRequest);
//					if (result.getCustomer() != null) {
//                        String customerId = result.getCustomer().getId();
//                        logger.info("Customer created successfully with ID: {}", customerId);
//                        return ResponseEntity.ok(new CustomerResponse(customerId, "Customer created successfully"));
//                    } else {
//                        logger.error("Failed to create customer - no customer returned from Square API");
//                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body(new CustomerResponse(null, "Failed to create customer"));
//                    }
//				} catch (ApiException e) {
//					  logger.error("ApiException: ", e);
//		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//			                .body(new CustomerResponse(null, "ApiException during customer creation: " + e.getMessage()));
//				}
//	 }

}
