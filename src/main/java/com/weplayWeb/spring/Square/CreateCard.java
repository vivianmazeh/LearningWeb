package com.weplayWeb.spring.Square;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.squareup.square.SquareClient;
import com.squareup.square.api.CardsApi;
import com.squareup.square.models.Card;
import com.squareup.square.models.CreateCardRequest;
import com.squareup.square.models.CreateCardResponse;

import jakarta.annotation.PostConstruct;

@Service
public class CreateCard {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateOrder.class);
	
	@Autowired 
	private SquareClient squareClient;
	
	@Value("${square.locationId}")
    private String locationId;
	
	private CardsApi cardsApi;
	
	@PostConstruct
    private void init() {  
		
        this.cardsApi = squareClient.getCardsApi();
    }
  
	 public String createCard(String customer_id, String source_id) throws InterruptedException, ExecutionException {
		 

		Card card = new Card.Builder()
		  .customerId(customer_id)
		  .build();

		CreateCardRequest body = new CreateCardRequest.Builder(UUID.randomUUID().toString(), 
															   source_id, 
															   card)
															.build();


			CreateCardResponse response;
			try {
				response = cardsApi.createCardAsync(body).get();
				return response.getCard().getId();
			} catch (InterruptedException e) {
				  logger.error("InterruptedException occurs while creating card" + e.getMessage());
		     
		           throw e;
			} catch (ExecutionException e) {
				logger.error("ExecutionException occurs while creating card " + e.getMessage());
		           throw e;
			}
			
		 
	 }
}
