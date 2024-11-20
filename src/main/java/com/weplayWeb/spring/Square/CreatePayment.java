package com.weplayWeb.spring.Square;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.squareup.square.SquareClient;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CreatePaymentResponse;
import com.squareup.square.models.Money;
import com.weplayWeb.spring.services.CSPService;
import com.weplayWeb.spring.services.EmailService;

@Service
public class CreatePayment {
	
	private static final Logger logger = LoggerFactory.getLogger(CreatePayment.class);
	
	@Autowired 
	private SquareClient squareClient;
	
	@Autowired
    private CSPService cspService;
	
	@Autowired
	private EmailService emailService;
	
	
	@Value("${square.locationId}")
    private String locationId;

	private PaymentsApi paymentsApi; 

	@Autowired
    private CreateTicketOrder createOrder;

	private Money money;
	
	private String orderId;
	
	private CreatePaymentResponse paymentResponse;
	
	private String receiptUrl;
	
	private TokenWrapper data;
	
	
	public CreatePayment() {}
	
	 public ResponseEntity<PaymentResult> createPaymentRequest(TokenWrapper tokenObject) throws IOException, ApiException{
		 
		  if (!isValidRequest(tokenObject)) {
	            return handleInvalidRequest();
	        }
		 data = tokenObject;
		 money = createMoney(tokenObject);
		 
		 String nonce = cspService.generateNonce();   
	     paymentsApi = squareClient.getPaymentsApi(); 
	    
		    orderId = createOrder.createTicketOrder(tokenObject);
				
	    	   // Build the CreatePaymentRequest with orderId
	            CreatePaymentRequest paymentRequest = new CreatePaymentRequest.Builder(
	                    tokenObject.getSourceId(),  // Token from frontend
	                    UUID.randomUUID().toString()) // Idempotency Key
	                    .amountMoney(money)
	                    .customerId(tokenObject.getCustomerId())             
	                    .locationId(locationId)
	                    .orderId(orderId)  // Link payment to order
	                    .autocomplete(true) // Automatically complete payment
	                    .buyerEmailAddress(tokenObject.getBuyerEmailAddress()) // Include email for receipt
	                    .build();
	         try {
	            paymentResponse =paymentsApi.createPayment(paymentRequest);
	          
	            receiptUrl = paymentResponse.getPayment().getReceiptUrl();
	           
	          
	         return buildSuccessResponse(nonce);
	         
	     } catch (ApiException e) {
	    	 return handleApiException(e);
	     } catch (IOException e) {
	             return handleIOException(e);
	     } catch (Exception e) {
            return handleGeneralException(e);
        }
	 }	 
	 
	   public EmailResult sendEmail() {
	        try {
	            if (emailService == null) {
	                logger.error("EmailService is not initialized");
	                return new EmailResult("FAILURE", "Email service not available");
	            }
	            return emailService.sendOrderConfirmationEmail(
	                orderId,
	                paymentResponse.getPayment(),
	                data,
	                receiptUrl
	            );
	        } catch (Exception e) {
	            logger.error("Failed to send email", e);
	            return new EmailResult("FAILURE", "Failed to send email: " + e.getMessage());
	        }
	    }
	    
	 
	 private boolean isValidRequest(TokenWrapper tokenObject) {
	        return tokenObject != null 
	            && tokenObject.getSourceId() != null          
	            && tokenObject.getAmountMoney() != null;
	           
	    }
	 	
	 
	   private ResponseEntity<PaymentResult> handleInvalidRequest() {
	        logger.error("Invalid payment request received");
	        String nonce = cspService.generateNonce();
	        return ResponseEntity.badRequest()
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("FAILURE", "Invalid payment request"));
	    }
	   
	   private HttpHeaders createHeadersWithCSP(String nonce) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Content-Security-Policy", cspService.generateCSPHeader(nonce));
	        headers.set("X-CSP-Nonce", nonce);
	        return headers;
	    }
	   
	   private ResponseEntity<PaymentResult> buildSuccessResponse(String nonce) {
	        logger.info("Payment processed successfully");
	        PaymentResult result = new PaymentResult("SUCCESS", null);
	        result.setNonce(nonce);
	        
	        return ResponseEntity.ok()
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("SUCCESS", null));	        
	    }
	   
	   private Money createMoney(TokenWrapper tokenObject) {
		   	   
		   return new Money.Builder()
		             .amount(tokenObject.getAmountMoney().getAmount()) // Amount in cents
		             .currency("USD") // Ensure it's the correct currency
		             .build();

	   }

	 
	   
	   private ResponseEntity<PaymentResult> handleApiException(ApiException e) {
	        logger.error("Square API Exception during payment processing: ", e);
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
	                e.getErrors().toString() : "Payment processing failed"));
	    }
	    
	    private ResponseEntity<PaymentResult> handleIOException(IOException e) {
	        logger.error("IO Exception during payment processing: ", e);
	      
      
	        String nonce = cspService.generateNonce();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("FAILURE", "Payment processing failed due to IO error"));
	    }
	    
	    private ResponseEntity<PaymentResult> handleGeneralException(Exception e) {
	        logger.error("Unexpected error during payment processing: ", e);
	    
         
	        String nonce = cspService.generateNonce();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("FAILURE", "An unexpected error occurred"));
	    }
	    
}
