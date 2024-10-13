package com.weplayWeb.spring.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.squareup.square.api.PaymentsApi;
import com.squareup.square.exceptions.ApiException;
import com.squareup.square.models.CreatePaymentRequest;
import com.squareup.square.models.Money;
import com.squareup.square.models.RetrieveLocationResponse;
import com.weplayWeb.spring.Square.CreatePayment;
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
	
	@Value("${cors.allowed.origin}")
	private String corsAllowedOrigin;
	
	   // Inject CreatePayment dependency
    private final CreatePayment createPayment;
	
	public Controller(CreatePayment createPayment) {
		this.createPayment = createPayment;
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

        try {
        	 PaymentResult paymentResult = createPayment.process(tokenObject);
             return ResponseEntity.ok(paymentResult);
        } catch (ApiException e) {
            e.printStackTrace();
         
            return ResponseEntity.status(403).body(new PaymentResult("FAILURE", e.getErrors()));
        }
    }
	
	 @GetMapping("/test")
	    public String corsDebug() {
		    System.out.print("CORS Allowed Origin: " + corsAllowedOrigin);
	        return "CORS Allowed Origin: " + corsAllowedOrigin;
	    }
}
