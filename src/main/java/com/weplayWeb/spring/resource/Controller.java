package com.weplayWeb.spring.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.weplayWeb.spring.Square.CreateCustomer;
import com.weplayWeb.spring.Square.CreatePayment;
import com.weplayWeb.spring.Square.CustomerResponse;
import com.weplayWeb.spring.Square.ErrorResponse;
import com.weplayWeb.spring.Square.PaymentResult;
import com.weplayWeb.spring.Square.TokenWrapper;
import com.weplayWeb.spring.model.CityProfile;
import com.weplayWeb.spring.polulationData.GetCityProfiles;
import com.weplayWeb.spring.services.CSPService;

@RestController
@RequestMapping("/api")
public class Controller {

	// @Autowired: used for automatic dependency injection

	@Autowired
    private CSPService cspService;
	
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
		
	 @GetMapping("/nonce")
	    public ResponseEntity<Map<String, String>> getNonce() {
	        String nonce = cspService.generateNonce();
	            
	        return ResponseEntity.ok(Collections.singletonMap("nonce", nonce));
	    }
	 
	
	  @PostMapping("/customer") 
	  public ResponseEntity<CustomerResponse> createCustomer(@RequestBody TokenWrapper tokenObject)
	          throws InterruptedException, ExecutionException, IOException {
		  	 
			  logger.info("Received customer creation request");	  
			  ResponseEntity<CustomerResponse> response = createCustomer.createCustomerResponse(tokenObject);
			  logger.info("Customer creation completed with status: {}", response.getStatusCode());
	          return response;		  
	    } 
	      
   
	    @PostMapping("/payment") 
	    public ResponseEntity<PaymentResult> processPayment(@RequestBody TokenWrapper tokenObject)
	            throws InterruptedException, ExecutionException, IOException {   
	    	
	    	     logger.info("Received Payment creation request");	
	    		 return createPayment.createPaymentRequest(tokenObject);		      		
	     }
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


