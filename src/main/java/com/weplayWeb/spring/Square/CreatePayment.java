package com.weplayWeb.spring.Square;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.squareup.square.SquareClient;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CustomerDetails;
import com.squareup.square.models.Money;
import com.weplayWeb.spring.services.CSPService;

@Service
public class CreatePayment {
	
	private static final Logger logger = LoggerFactory.getLogger(CreatePayment.class);
	@Autowired 
	private SquareClient squareClient;
	
	@Autowired
    private CSPService cspService;
	
	@Value("${square.locationId}")
    private String locationId;

	private PaymentsApi paymentsApi; 

	private Money money;
	
	
	public CreatePayment() {}
	
	 public ResponseEntity<PaymentResult> createPaymentRequest(TokenWrapper tokenObject) throws IOException{
		 
		  if (!isValidRequest(tokenObject)) {
	            return handleInvalidRequest();
	        }
		  
		  String nonce = cspService.generateNonce();   
		paymentsApi = squareClient.getPaymentsApi();
			
			// Create Money object for the payment
	      money = new Money.Builder()
	             .amount(tokenObject.getAmountMoney().getAmount()) // Amount in cents
	             .currency("USD") // Ensure it's the correct currency
	             .build();

		// Build the CreatePaymentRequest
	     CreatePaymentRequest paymentRequest = new CreatePaymentRequest.Builder(
	             tokenObject.getSourceId(),  // Token from frontend
	             UUID.randomUUID().toString()) // Idempotency Key
	             .amountMoney(money)
	             .customerId(tokenObject.getCustomerId())             
	             .locationId(locationId)
	             .autocomplete(true) // Automatically complete payment
	             .build();
	     
	     try {
	         // Call the Square Payments API
	         paymentsApi.createPayment(paymentRequest);
	         return buildSuccessResponse(nonce);
	         
	     } catch (ApiException e) {
	    	 return handleApiException(e);
	     } catch (IOException e) {
	    	 return handleIOException(e);
			
		}catch (Exception e) {
            return handleGeneralException(e);
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
	        return ResponseEntity.ok()
	            .headers(createHeadersWithCSP(nonce))
	            .body(new PaymentResult("SUCCESS", null));
	    }
	   
	   private ResponseEntity<PaymentResult> handleApiException(ApiException e) {
	        logger.error("Square API Exception during payment processing: ", e);
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
