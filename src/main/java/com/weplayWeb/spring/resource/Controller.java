package com.weplayWeb.spring.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.weplayWeb.spring.Square.CreateCustomer;
import com.weplayWeb.spring.Square.CreatePayment;
import com.weplayWeb.spring.Square.ErrorResponse;
import com.weplayWeb.spring.Square.TokenWrapper;
import com.weplayWeb.spring.model.CityProfile;
import com.weplayWeb.spring.polulationData.GetCityProfiles;

@RestController
public class Controller {

	// @Autowired: used for automatic dependency injection

	
	@Autowired
	private CreateCustomer createCustomer;
	
	@Autowired
	private CreatePayment createPayment;
	
	private final Logger logger = LoggerFactory.getLogger(Controller.class);
	
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
	
	  public Controller() {}
	  

	@GetMapping("/cityprofiles/{state_name}")
	public ArrayList<CityProfile> getCityProfile(@PathVariable String state_name){
		state_name = state_name.substring(0, 1).toUpperCase() + state_name.substring(1).toLowerCase(); // make sure the first char is away capitalized 
		GetCityProfiles data = new GetCityProfiles(state_name);
		return data.getCityProfile();	
	}
			
	
	  @PostMapping("/customer") 
	  public ResponseEntity<?> createCustomer(@RequestBody TokenWrapper tokenObject)
	          throws InterruptedException, ExecutionException, IOException {
		  
		  try {
			  		  
			  return createCustomer.createCustomerResponse(tokenObject);
			  
		  } catch (IOException e) {
	            logger.error("IO error during customer creation", e);
	            return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ErrorResponse("IO error occurred during customer creation"));
	        } catch (Exception e) {
	            logger.error("Unexpected error during customer creation", e);
	            return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ErrorResponse("An unexpected error occurred during customer creatoin"));
	        }
	    } 
	      
   
	    @PostMapping("/payment") 
	    public ResponseEntity<?> processPayment(@RequestBody TokenWrapper tokenObject)
	            throws InterruptedException, ExecutionException {
	    	
	    	try { 		   	
	    	
	    		 return createPayment.createPaymentRequest(tokenObject);		  
		    	
	    	}catch (IOException e) {
	            logger.error("IO error during payment creation", e);
	            return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ErrorResponse("IO error occurred during payment creation"));
	        } catch (Exception e) {
	            logger.error("Unexpected error during payment creation", e);
	            return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ErrorResponse("An unexpected error occurred during payment processing"));
	        }
	     }
	
//	  private CompletableFuture<RetrieveLocationResponse> getLocationInformation(
//	      SquareClient squareClient) {
//	    return squareClient.getLocationsApi().retrieveLocationAsync(locationId)
//	        .thenApply(result -> {
//	          return result;
//	        })
//	        .exceptionally(exception -> {
//	          System.out.println("Failed to make the request");
//	          System.out.printf("Exception: %s%n", exception.getMessage());
//	          return null;
//	        });
//	  }

}

@RestControllerAdvice
class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Unhandled exception occurred", e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("An unexpected error occurred"));
    }
}


