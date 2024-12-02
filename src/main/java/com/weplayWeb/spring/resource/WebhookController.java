package com.weplayWeb.spring.resource;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weplayWeb.spring.services.EmailService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {
	
	 private final Logger logger = LoggerFactory.getLogger(WebhookController.class);

		
	    @Value("${square.webhook.signingKey}")
	    private String webhookSigningKey;
	    
	     
	    @Autowired
	    private EmailService emailService;
	    
	    
	    @Value("${square.environment}")
	    private String squareEnvironment;
	    
	    @Autowired
	    private Environment environment;

	    private final Map<String, Long> processedInvoices = new ConcurrentHashMap<>();
	    private static final long DUPLICATE_WINDOW_MS = 10000; // 10 seconds window
	   
	    @PostMapping("/square")
	    public ResponseEntity<String> handleSquareWebhook(
	    		 @RequestHeader(value = "X-Square-Signature", required = true) String signature,
	             @RequestHeader(value = "Square-Environment", required = false) String squareEnv,
	             @RequestBody String payload) throws JsonMappingException, JsonProcessingException {
	    	
	    	  
	        String activeProfile = Arrays.toString(environment.getActiveProfiles());
	             
	        try {
       
	            // Parse the webhook event
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode event = mapper.readTree(payload);
	            
	            long currentTime = System.currentTimeMillis();
	           
	            String invoiceId = event.path("data")
	                    .path("object")
	                    .path("invoice")
	                    .path("id")
	                    .asText();
	            
	            logger.info("Invoice ID: {}", invoiceId);
	            // Check if webhook was processed recently
	            Long lastProcessed = processedInvoices.get(invoiceId);
	            if (lastProcessed != null ) {
	            	 long timeDiff = currentTime - lastProcessed;
	            	 logger.info("Time between duplicate invoices: {} ms for ID: {}", timeDiff, invoiceId);
	                 if (timeDiff < DUPLICATE_WINDOW_MS) {
	                     logger.info("Skipping duplicate webhook received within {} ms: {}", timeDiff, invoiceId);
	                     return ResponseEntity.ok("Webhook already processed");
	                 }
	            }

	            processedInvoices.put(invoiceId, currentTime);
	            
	            String eventType = event.path("type").asText();
	           
	            logger.info("Processing webhook event type: {}", eventType);
	            
	            if ("invoice.payment_made".equals(eventType)) {
	                // Navigate to subscription data using the correct path
	                JsonNode invoiceNode = event.path("data")
	                    .path("object")
	                    .path("invoice");
	                
	                if (invoiceNode.isMissingNode()) {
	                    logger.error("Missing invoice data in webhook payload");
	                    return ResponseEntity.badRequest().body("Missing subscription data");
	                }
	                
	                handleSubscriptionCreated(invoiceNode);
	               
	                return ResponseEntity.ok("Webhook processed successfully");
	            }
	            return ResponseEntity.ok("Webhook processed");
	        } catch (Exception e) {
	            logger.error("Error processing webhook", e);
	            return ResponseEntity.internalServerError().body("Error processing webhook");
	        }
	    }	
	    
	    private void handleSubscriptionCreated(JsonNode invoiceNodeData) {
	        try {
	        	  String invoiceId = invoiceNodeData.path("id").asText();	   
	        	  String status = invoiceNodeData.path("status").asText();
	        	  String invoiceNumber = invoiceNodeData.path("invoice_number").asText();
	        	  String subscriptionId = invoiceNodeData.path("subscription_id").asText();
	        	  String title = invoiceNodeData.path("title").asText();
	        	 		       	
	            if (invoiceId.isEmpty()) {
	                logger.error("invoice ID is missing in webhook data");
	                return;
	            }
	 
	            String recipientEmail = invoiceNodeData.path("primary_recipient").path("email_address").asText();
	            String recipientGivenName = invoiceNodeData.path("primary_recipient").path("given_name").asText();
	            String recipientFamilyName = invoiceNodeData.path("primary_recipient").path("family_name").asText();
	            
	            if (!recipientGivenName.isEmpty()) {
	                recipientGivenName = recipientGivenName.substring(0, 1).toUpperCase() + recipientGivenName.substring(1).toLowerCase();
	            }
	           
	            if (!recipientFamilyName.isEmpty()) {
	                recipientFamilyName = recipientFamilyName.substring(0, 1).toUpperCase() + recipientFamilyName.substring(1).toLowerCase();
	            }
	           
	            if(status.equals("PAID")) {
	            	 // Send email with cancellation link
	            	 logger.info("Sending email for paid invoice: {}", invoiceId);
		            emailService.sendSubscriptionEmail(
		            		recipientEmail,
		            		recipientGivenName,
		            		recipientFamilyName,
		            		invoiceNumber,
		            		subscriptionId,
		            		title
		            );
		            logger.info("Email sent for invoice: {}", invoiceId);
	            }
	           
	        } catch (Exception e) {
	            logger.error("Error handling subscription creation", e);
	        }
	    }
	    

}
