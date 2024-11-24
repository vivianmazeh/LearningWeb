package com.weplayWeb.spring.Square;

import java.util.LinkedList;

import com.squareup.square.api.OrdersApi;
import com.squareup.square.SquareClient;
import com.squareup.square.api.CustomersApi;
import com.squareup.square.api.SubscriptionsApi;
import com.squareup.square.models.*;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class CreateSubscription {
	
	@Autowired
    private SquareClient squareClient;
    
    @Value("${square.locationId}")
    private String locationId;
    
    
    private SubscriptionsApi subscriptionsApi;
    
    private CustomersApi customersApi;
    
    public CreateSubscription() {}
    
    @PostConstruct
    private void init() {
        this.subscriptionsApi = squareClient.getSubscriptionsApi();
        this.customersApi = squareClient.getCustomersApi();
    }

    public ResponseEntity<?> createSubscription(SubscriptionRequest request) {
    	
    	String customer_id = request.getCustomerId();
        try {
            // Create subscription plans based on number of children
            Money planAmount = new Money.Builder()
                .amount(request.getNumberOfChildren() == 2 ? 11500L : 
                       request.getNumberOfChildren() == 3 ? 16500L : 19500L)
                .currency("USD")
                .build();
            
            SubscriptionSource source = new SubscriptionSource.Builder()
            		  .name("My Application")
            		  .build();
            
//            Phase phase = new Phase.Builder()
//            		  .ordinal(0L)
//            		  .orderTemplateId(order_id)
//            		  .build(); 
//            
            
            // Create subscription phases
            SubscriptionPhase phase = new SubscriptionPhase.Builder(locationId)
                .cadence("MONTHLY")           
                .recurringPriceMoney(new Money.Builder()
                    .amount(planAmount.getAmount())
                    .currency("USD")
                    .build())
                .build();

            // Create subscription
            CreateSubscriptionRequest subscriptionRequest = new CreateSubscriptionRequest.Builder(locationId, customer_id)
                .idempotencyKey(UUID.randomUUID().toString())
                .locationId(locationId)
           //     .planId(request.getPlanId())
                .customerId(request.getCustomerId())
                .startDate(request.getStartDate())
                .cardId(request.getCardId())
                .timezone("America/Los_Angeles")
                .source(source)
       //         .phases(Collections.singletonList(phase))
                .build();

            return ResponseEntity.ok(subscriptionsApi.createSubscription(subscriptionRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create subscription: " + e.getMessage());
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
}
