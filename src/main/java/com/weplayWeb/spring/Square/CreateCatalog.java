package com.weplayWeb.spring.Square;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CatalogApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CatalogObject;
import com.squareup.square.models.CatalogSubscriptionPlan;
import com.squareup.square.models.CatalogSubscriptionPlanVariation;
import com.squareup.square.models.Money;
import com.squareup.square.models.SubscriptionPhase;
import com.squareup.square.models.SubscriptionPricing;
import com.squareup.square.models.UpsertCatalogObjectRequest;

import jakarta.annotation.PostConstruct;

@Service
public class CreateCatalog {

	private static final Logger logger = LoggerFactory.getLogger(CreateCatalog.class);
	
	 @Autowired
	 private SquareClient squareClient;
	 
	 private CatalogApi catalogApi;
	 
	 CatalogObject result;
	 CatalogObject variationResult;
	
	  @PostConstruct
	    private void init() {  
	        this.catalogApi = squareClient.getCatalogApi();
	    }
	  
	    public String createSubscriptionPlan(TokenWrapper tokenObject) {
	        try {
	        	
	            // Calculate price based on number of children 
	            long planAmount =(long) tokenObject.getOrderInfo().get(0).price() * 100;
	            Integer numberOfChildren = tokenObject.getOrderInfo().get(0).numberOfChildrenAllowed();
	            String planName = String.format("WePlay Membership - %d Children", numberOfChildren);
	
	            Money recurringPriceMoney = new Money.Builder()
	            		.amount(planAmount)
	            		.currency("USD")
	            		.build();
	            
	            SubscriptionPricing pricing = new SubscriptionPricing.Builder()
	            		  .type("STATIC")
	            		  .priceMoney(recurringPriceMoney)
	            		  .build();
	            
	            SubscriptionPhase phase = new SubscriptionPhase.Builder("MONTHLY")            
	                .periods(null)
	                .ordinal(0L)
	                .pricing(pricing)
	                .build();

	            LinkedList<SubscriptionPhase> phases = new LinkedList<>();
	            phases.add(phase);
	            

	            // Create catalog subscription plan
	            String planId = "#WEPLAY_" + UUID.randomUUID().toString().replace("-", "");
	           

	            
	            CatalogObject subscriptionPlan = new CatalogObject.Builder("SUBSCRIPTION_PLAN", planId)
	                    .presentAtAllLocations(true)
	                    .subscriptionPlanData(new CatalogSubscriptionPlan.Builder(planName)  // Added planName parameter
	                        .phases(phases)
	                        .build())
	                    .build();
	            logger.info("Creating subscription plan with ID: {}", planId);
	           
	            UpsertCatalogObjectRequest planRequest = new UpsertCatalogObjectRequest.Builder(
	            		UUID.randomUUID().toString(), // idempotencyKey
	                    subscriptionPlan             // object
	                ).build();

	            CatalogObject createdPlan = catalogApi.upsertCatalogObject(planRequest).getCatalogObject();
	            if (createdPlan == null) {
                    throw new RuntimeException("Created plan is null");
                }
                logger.info("Successfully created plan with ID: {}", createdPlan.getId());
                
                String variationId = "#WEPLAY_VAR_" + UUID.randomUUID().toString().replace("-", "");
	            CatalogObject createdVariation;
	            
	            CatalogSubscriptionPlanVariation subscriptionPlanVariationData = new CatalogSubscriptionPlanVariation.Builder(planName, phases)
	            		  .subscriptionPlanId(createdPlan.getId())
	            		  .build();
	            // Create plan variation
	            CatalogObject planVariation = new CatalogObject.Builder("SUBSCRIPTION_PLAN_VARIATION", variationId)
	                .presentAtAllLocations(true)
	                .subscriptionPlanVariationData(subscriptionPlanVariationData)
	                .build();
	            // Create variation in catalog
	            UpsertCatalogObjectRequest variationRequest = new UpsertCatalogObjectRequest.Builder(
	                UUID.randomUUID().toString(),
	                planVariation
	            ).build();
	            
	            try {
	                createdVariation = catalogApi.upsertCatalogObject(variationRequest).getCatalogObject();
	            } catch (ApiException e) {
	                logger.error("Square API Error: Status Code: {}, Response Body: {}", 
	                    e.getResponseCode(), e.getHttpContext().getResponse().getBody());
	                throw e;
	            }
         
                logger.info("Successfully created plan variation with ID: {}", createdVariation.getId());
	
	            // Return the variation ID for subscription creation
	            return createdVariation.getId();
	        } catch (Exception e) {
	            logger.error("Failed to create subscription plan", e);
	   
	            throw new RuntimeException("Failed to create subscription plan: " + e.getMessage(), e);
	        }
	    } 
}
