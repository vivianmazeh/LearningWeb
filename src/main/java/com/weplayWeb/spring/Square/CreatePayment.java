package com.weplayWeb.spring.Square;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.squareup.square.SquareClient;
import com.squareup.square.api.PaymentsApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.CustomerDetails;
import com.squareup.square.models.Money;

@Service
public class CreatePayment {
	
	@Autowired 
	private SquareClient squareClient;
	
	@Value("${square.locationId}")
    private String locationId;

	private PaymentsApi paymentsApi; 

	private Money money;
	
	
	public CreatePayment() {}
	
	 public ResponseEntity<PaymentResult> createPaymentRequest(TokenWrapper tokenObject) throws IOException{
		 
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
	         return ResponseEntity.ok(new PaymentResult("SUCCESS", null));
	     } catch (ApiException e) {
	         return ResponseEntity.status(403).body(new PaymentResult("FAILURE", e.getErrors()));
	     } catch (IOException e) {
	    	 e.printStackTrace();
	    	 return ResponseEntity.status(500).body(new PaymentResult("Unknown error occurred", null));
			
		}
	 }	       
}
