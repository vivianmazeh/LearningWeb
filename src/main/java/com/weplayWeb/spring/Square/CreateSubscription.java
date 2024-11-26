package com.weplayWeb.spring.Square;

import java.util.LinkedList;
import java.util.List;

import com.squareup.square.api.OrdersApi;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.api.CustomersApi;
import com.squareup.square.api.SubscriptionsApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.*;
import com.weplayWeb.spring.services.CSPService;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Service
public class CreateSubscription {
	
	final Logger logger = LoggerFactory.getLogger(CreateSubscription.class);
	@Autowired
    private SquareClient squareClient;
    
    @Value("${square.locationId}")
    private String location_id;
    
    @Autowired
    private CSPService cspService;
    
	@Autowired
    private CreateOrder createOrder;
	
	@Autowired
    private CreateCatalog createCatalog;
    
	
    private SubscriptionsApi subscriptionsApi;  


    private String orderId;
    String planVariationId;
    
    
    public CreateSubscription() {}
    
    @PostConstruct
    private void init() {
        this.subscriptionsApi = squareClient.getSubscriptionsApi();
       
    }

    public ResponseEntity<PaymentResult> createSubscription(TokenWrapper tokenObject) throws ApiException {
    	
    	 	
    	String customer_id = tokenObject.getCustomerId();	   		
    	String sourceId = tokenObject.getSourceId();   
    	String nonce = cspService.generateNonce(); 
        logger.info("Creating subscription for customer: {}", customer_id);
        logger.info("Using source ID: {}", sourceId);
		
        String planVariationId;
  
		  try {
			  
			  orderId = createOrder.createSubscriptionOrder(tokenObject);
	   		
			  planVariationId = createCatalog.createSubscriptionPlan(tokenObject);
			  logger.info("Created plan variation with ID: {}", planVariationId);
            // Create subscription phases
			  Phase phase = new Phase.Builder()
            			.ordinal(0L)       		    
            			.orderTemplateId(orderId)
            			
		                .build();
            
			  LinkedList<Phase> phases = new LinkedList<>();
			  phases.add(phase);

            
            // Create subscription
			  CreateSubscriptionRequest subscriptionRequest = new CreateSubscriptionRequest.Builder(location_id, customer_id)
                .idempotencyKey(UUID.randomUUID().toString()) 
                .planVariationId(planVariationId)
                .timezone("America/New_York")
                .phases( phases)                             
                .build();

            subscriptionsApi.createSubscriptionAsync(subscriptionRequest);
          
            return buildSuccessResponse(nonce);
        } catch (ApiException e) {
	    	 return handleApiException(e);
	     } catch (IOException e) {
	             return handleIOException(e);
	     } catch (Exception e) {
           return handleGeneralException(e);
       }
    }

    public ResponseEntity<?> cancelSubscription(String subscriptionId) {
        try {
            CancelBookingRequest request = new CancelBookingRequest.Builder()
                .build();
            
            return ResponseEntity.ok(subscriptionsApi.cancelSubscription(subscriptionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to cancel subscription: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getSubscription(String subscriptionId) {
        try {
            return ResponseEntity.ok(subscriptionsApi.retrieveSubscription(subscriptionId, subscriptionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve subscription: " + e.getMessage());
        }
     }
        
 	   private ResponseEntity<PaymentResult> buildSuccessResponse(String nonce) {
 		  logger.info("Payment processed successfully");
	        PaymentResult result = new PaymentResult("SUCCESS", null);
	        result.setNonce(nonce);
	        
	        return ResponseEntity.ok()
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("SUCCESS", null));	        
	    }
 	   
 	   private ResponseEntity<PaymentResult> handleApiException(ApiException e) {
	        logger.error("Square API Exception during subscription processing: ", e);
	        logger.error("Response Code: {}", e.getResponseCode());
          
           
           if (e.getHttpContext() != null) {
               logger.error("Request Headers: {}", e.getHttpContext().getRequest().getHeaders());
               logger.error("Response Headers: {}", e.getHttpContext().getResponse().getHeaders());
         
           }
           
           if (e.getErrors() != null && !e.getErrors().isEmpty()) {
               e.getErrors().forEach(error -> {
                   logger.error("Error Category: {}", error.getCategory());
                   logger.error("Error Code: {}", error.getCode());
                   logger.error("Error Detail: {}", error.getDetail());
                   logger.error("Error Field: {}", error.getField());
               });
           }
           
	        String nonce = cspService.generateNonce();
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("FAILURE", e.getErrors() != null ? 
	                e.getErrors().toString() : "subscription processing failed"));
	    }
	    
	    private ResponseEntity<PaymentResult> handleIOException(IOException e) {
	        logger.error("IO Exception during subscription processing: ", e);
	      
     
	        String nonce = cspService.generateNonce();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("FAILURE", "subscription Payment processing failed due to IO error"));
	    }
	    
	    private ResponseEntity<PaymentResult> handleGeneralException(Exception e) {
	        logger.error("Unexpected error during subscription: ", e);
	    
        
	        String nonce = cspService.generateNonce();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("FAILURE", "An unexpected error occurred"));
	    }
	    
	    private HttpHeaders createHeadersWithCSP(String nonce) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Content-Security-Policy", cspService.generateCSPHeader(nonce));
	        headers.set("X-CSP-Nonce", nonce);
	        return headers;
	    }
    	
}
