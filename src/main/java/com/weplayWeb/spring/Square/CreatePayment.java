package com.weplayWeb.spring.Square;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import com.squareup.square.models.CreateOrderRequest;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CreatePaymentResponse;
import com.squareup.square.models.Fulfillment;
import com.squareup.square.models.FulfillmentPickupDetails;
import com.squareup.square.models.Money;
import com.squareup.square.models.Order;
import com.squareup.square.models.OrderLineItem;
import com.weplayWeb.spring.services.CSPService;
import com.squareup.square.api.OrdersApi;

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

	private OrdersApi ordersApi;

	private Money money;
	
	
	public CreatePayment() {}
	
	 public ResponseEntity<PaymentResult> createPaymentRequest(TokenWrapper tokenObject) throws IOException{
		 
		  if (!isValidRequest(tokenObject)) {
	            return handleInvalidRequest();
	        }
		  
		 money = createMoney(tokenObject);
		 
		 String nonce = cspService.generateNonce();   
	     paymentsApi = squareClient.getPaymentsApi();
	     ordersApi = squareClient.getOrdersApi();
	     
	    
	     try {
		    String orderId = createTicketOrder(tokenObject);
		    logger.info("Order created with ID: " + orderId);
				
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
 
	            CreatePaymentResponse paymentResponse =paymentsApi.createPayment(paymentRequest);
	          
	            logger.info("Square will send receipt to: {}", 
	                    tokenObject.getBuyerEmailAddress());
	         return buildSuccessResponse(nonce);
	         
	     } catch (ApiException e) {
	    	 return handleApiException(e);
	     } catch (IOException e) {
	             return handleIOException(e);
	     } catch (Exception e) {
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
	   
	   private Money createMoney(TokenWrapper tokenObject) {
		   	   
		   return new Money.Builder()
		             .amount(tokenObject.getAmountMoney().getAmount()) // Amount in cents
		             .currency("USD") // Ensure it's the correct currency
		             .build();

	   }

	   private String createTicketOrder(TokenWrapper tokenObject) throws ApiException, IOException {
	        // Create order line item
	        OrderLineItem lineItem = new OrderLineItem.Builder("1")
	                .basePriceMoney(money)
	                .name("Indoor Playground Admission")
	                .build();

	        // Create fulfillment info
	        Fulfillment fulfillment = new Fulfillment.Builder()
	                .type("PICKUP")  // Customer will pick up/check-in at location
	                .state("PROPOSED") // Initial state
	                .pickupDetails(new FulfillmentPickupDetails.Builder()
	                    .note("Valid for one-time admission. Present order number at check-in.")
	                    .build())
	                .build();

	        // Create the order
	        Order order = new Order.Builder(locationId)
	                .lineItems(Collections.singletonList(lineItem))
	              //  .fulfillments(Collections.singletonList(fulfillment))
//	                .metadata(new HashMap<String, String>() {{
//	                    put("customer_email", tokenObject.getBuyerEmailAddress());
//	                    put("customer_firstName", tokenObject.getCustomer().getGivenName());
//	                    put("customer_lastName", tokenObject.getCustomer().getFamilyName());
//	                    put("customer_phoneNumber", tokenObject.getCustomer().getPhoneNumber());
//	                }})
	                .customerId(tokenObject.getCustomerId())	                
	                .state("OPEN")
	                .build();

	        CreateOrderRequest orderRequest = new CreateOrderRequest.Builder()
	                .order(order)
	                .idempotencyKey(UUID.randomUUID().toString())
	                .build();

	        return ordersApi.createOrder(orderRequest).getOrder().getId();
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
