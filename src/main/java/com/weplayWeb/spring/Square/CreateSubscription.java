package com.weplayWeb.spring.Square;

import java.util.LinkedList;

import com.squareup.square.api.OrdersApi;
import com.squareup.square.models.CreateOrderRequest;
import com.squareup.square.models.Order;
import com.squareup.square.models.OrderLineItem;
import com.squareup.square.models.OrderLineItemDiscount;

public class CreateSubscription {
	
	private OrdersApi ordersApi;
	OrderLineItem orderLineItem;
	LinkedList<OrderLineItem> lineItems;
	OrderLineItemDiscount orderLineItemDiscount;
	LinkedList<OrderLineItemDiscount> discounts;
	Order order;
	
	public CreateSubscription() {
		 orderLineItem  = new OrderLineItem.Builder("1")
				  .catalogObjectId("KAQJXCONWUSBWPHR62W3DZTO")
				  .build();
		 
		 lineItems  = new LinkedList<>();
			
		lineItems.add(orderLineItem);
		OrderLineItemDiscount orderLineItemDiscount  = new OrderLineItemDiscount.Builder()
				  .catalogObjectId("5PFBH6YH5SB2F63FOIHJ7HWR")
				  .scope("ORDER")
				  .build();
		
		discounts = new LinkedList<>();
		discounts.add(orderLineItemDiscount);
		
		order = new Order.Builder("LE40N37TVF5FT")
				  .lineItems(lineItems)
				  .discounts(discounts)
				  .state("DRAFT")
				  .build();		
		
	}
	
	public void createOrderAsync() {
		
		CreateOrderRequest body = new CreateOrderRequest.Builder()
				  .order(order)
				  .idempotencyKey("27bb3655-950e-481d-ad33-1386587fbbcb")
				  .build();
		
		
		ordersApi.createOrderAsync(body).thenAccept(result -> {
		    System.out.println("Success!");
		  })
		  .exceptionally(exception -> {
		    System.out.println("Failed to make the request");
		    System.out.println(String.format("Exception: %s", exception.getMessage()));
		    return null;
		  });
				
	}
	
	

			

			

			

			

}
