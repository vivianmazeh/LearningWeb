package com.weplayWeb.spring.Square;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.squareup.square.SquareClient;
import com.squareup.square.api.OrdersApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreateOrderRequest;
import com.squareup.square.models.Fulfillment;
import com.squareup.square.models.FulfillmentPickupDetails;
import com.squareup.square.models.FulfillmentRecipient;
import com.squareup.square.models.Money;
import com.squareup.square.models.Order;
import com.squareup.square.models.OrderLineItem;


@Service 
public class CreateTicketOrder {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateTicketOrder.class);
	
	@Autowired 
	private SquareClient squareClient;
			
	@Value("${square.locationId}")
    private String locationId;
	
	private OrdersApi ordersApi;
	
	public CreateTicketOrder() {}

	
	  public String createTicketOrder(TokenWrapper tokenObject) throws ApiException, IOException {
		   
		  ordersApi = squareClient.getOrdersApi();
		  
		  
		   List<OrderLineItem> lineItems = new ArrayList<>();
	
		   
		   for (TokenWrapper.Order orderDetail : tokenObject.getOrderInfo()) {
		        OrderLineItem lineItem = new OrderLineItem.Builder(String.valueOf(orderDetail.quantity()))
		            .basePriceMoney(new Money.Builder()
		                .amount((long)(orderDetail.price() * 100)) // Convert to cents
		                .currency("USD")
		                .build())
		            .name(orderDetail.sectionName())
		            .build();
		        lineItems.add(lineItem);
		    }


	        String futureDate = OffsetDateTime.now().plusYears(3).toString();
	        
	        Fulfillment fulfillment = new Fulfillment.Builder()
	                .type("PICKUP")
	                .state("PROPOSED")// / Initial state - will change to COMPLETED upon check-in
	                .pickupDetails(new FulfillmentPickupDetails.Builder()
		                 .note("Valid for one-time admission. Present order number at check-in.")
		                 .recipient(new FulfillmentRecipient.Builder()
		                		 .emailAddress(tokenObject.getBuyerEmailAddress())
		                		 .displayName(tokenObject.getCustomer().getGivenName() + " " + tokenObject.getCustomer().getFamilyName())
		                		 .phoneNumber(tokenObject.getCustomer().getPhoneNumber())
		                		 
		                		 .build())
		                 .pickupAt(futureDate)
	                    .build())
	                .build();
	        
	 
	        // Create order with metadata
	        Map<String, String> metadata = new HashMap<>();
	        metadata.put("customer_name", tokenObject.getCustomer().getGivenName() + " " + tokenObject.getCustomer().getFamilyName());
	        metadata.put("customer_email", tokenObject.getBuyerEmailAddress());
	        
	        // Add order summary to metadata
	        StringBuilder orderSummary = new StringBuilder();
	        for (TokenWrapper.Order orderDetail : tokenObject.getOrderInfo()) {
	            orderSummary.append(orderDetail.sectionName())
	                       .append(": ")
	                       .append(orderDetail.quantity())
	                       .append(" x $")
	                       .append(String.format("%.2f", orderDetail.price()))
	                       .append(", ");
	        }
	        
	  
	        metadata.put("order_summary", orderSummary.toString().trim());
	        metadata.put("total_amount", String.format("$%.2f", tokenObject.getAmountMoney().getAmount()/ 100.0));
	        
	
			Order order = new Order.Builder(locationId)
	                .lineItems(lineItems)
	                .fulfillments(Collections.singletonList(fulfillment))
	                .metadata(metadata)
	                .customerId(tokenObject.getCustomerId())	                
	                .state("OPEN")
	                .build();

	        CreateOrderRequest orderRequest = new CreateOrderRequest.Builder()
	                .order(order)
	                .idempotencyKey(UUID.randomUUID().toString())
	                .build();

	        try {
	            return ordersApi.createOrder(orderRequest).getOrder().getId();
	        } catch (ApiException e) {
	           logger.info("Response Code: " + e.getResponseCode());
	           logger.info("Response Header: " + e.getHttpContext().getResponse().getHeaders());
	           logger.info("Errors: " + e.getErrors());
	            throw e;
	        }
	    }
	  

}
