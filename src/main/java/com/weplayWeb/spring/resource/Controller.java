package com.weplayWeb.spring.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.squareup.square.Environment;
import com.squareup.square.SquareClient;
import com.squareup.square.api.PaymentsApi;

import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.Money;
import com.squareup.square.models.RetrieveLocationResponse;

import com.weplayWeb.spring.Square.PaymentResult;
import com.weplayWeb.spring.Square.TokenWrapper;
import com.weplayWeb.spring.model.CityProfile;
import com.weplayWeb.spring.polulationData.GetCityProfiles;

@RestController
public class Controller {

	// @Autowired: used for automatic dependency injection
	@Autowired 
	private JdbcTemplate jdbctemplate;
	private XmlSqlQuerySource queries = new XmlSqlQuerySource() ;
	
	@Autowired 
	private SquareClient squareClient;
	
	 @Value("${square.environment}")
	    private String environment;

	 @Value("${square.accessToken}")
	    private String accessToken;

	@Value("${square.locationId}")
	    private String locationId;
	
	@Value("${square.applicationId}")
    private String applicationId;


	
	@Value("${cors.allowed.origin}")
	private String corsAllowedOrigin;
	
	   public Controller() {
		   
	
		try {
			queries.loadQueries();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

  
	
	  @GetMapping("/")
	  String index(Map<String, Object> model) throws InterruptedException, ExecutionException {

	    // Get currency and country for location
	    RetrieveLocationResponse locationResponse = getLocationInformation(squareClient).get();
	    model.put("paymentFormUrl",
	    		environment.equals("sandbox") ? "https://sandbox.web.squarecdn.com/v1/square.js"
	            : "https://web.squarecdn.com/v1/square.js");
	    model.put("locationId", locationId);
	    model.put("appId", applicationId);
	    model.put("currency", locationResponse.getLocation().getCurrency());
	    model.put("country", locationResponse.getLocation().getCountry());
	    model.put("idempotencyKey", UUID.randomUUID().toString());

	    return "index";
	  }

	@GetMapping("/cityprofiles/{state_name}")
	public ArrayList<CityProfile> getCityProfile(@PathVariable String state_name){
		state_name = state_name.substring(0, 1).toUpperCase() + state_name.substring(1).toLowerCase(); // make sure the first char is away capitalized 
		GetCityProfiles data = new GetCityProfiles(state_name);
		return data.getCityProfile();	
	}
	
	  /**
     * API endpoint for processing a payment.
     * Accepts a payment token from the frontend and processes the payment using Square API.
     *
     * @param tokenObject the token object containing the token, idempotency key, and other necessary info
     * @return PaymentResult indicating whether the payment was successful or not
     */
    @PostMapping("/payment") 
    public ResponseEntity<PaymentResult> processPayment(@RequestBody TokenWrapper tokenObject)
            throws InterruptedException, ExecutionException {

    	 PaymentsApi paymentsApi = squareClient.getPaymentsApi();
    	 // Create Money object for the payment
         Money money = new Money.Builder()
                 .amount(tokenObject.getAmountMoney().getAmount()) // Amount in cents
                 .currency("USD") // Ensure it's the correct currency
                 .build();
         
         // Build the CreatePaymentRequest
         CreatePaymentRequest paymentRequest = new CreatePaymentRequest.Builder(
                 tokenObject.getSourceId(),  // Token from frontend
                 UUID.randomUUID().toString()) // Idempotency Key
                 .amountMoney(money)
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
	
	 @GetMapping("/test")
	    public String corsDebug() {
		    System.out.print("CORS Allowed Origin: " + corsAllowedOrigin);
	        return "CORS Allowed Origin: " + corsAllowedOrigin;
	    }


	  /**
	   * Helper method that makes a retrieveLocation API call using the configured
	   * locationId and returns the future containing the response
	   *
	   * @param squareClient the API client
	   * @return a future that holds the retrieveLocation response
	   */
	  private CompletableFuture<RetrieveLocationResponse> getLocationInformation(
	      SquareClient squareClient) {
	    return squareClient.getLocationsApi().retrieveLocationAsync(locationId)
	        .thenApply(result -> {
	          return result;
	        })
	        .exceptionally(exception -> {
	          System.out.println("Failed to make the request");
	          System.out.printf("Exception: %s%n", exception.getMessage());
	          return null;
	        });
	  }

}

