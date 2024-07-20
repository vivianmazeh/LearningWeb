package com.learningWeb.spring.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
import com.learningWeb.spring.model.CityProfile;
import com.learningWeb.spring.polulationData.GetCityProfiles;

@RestController
@RequestMapping("/weplay")

public class Controller {

	// @Autowired: used for automatic dependency injection
	@Autowired 
	private JdbcTemplate jdbctemplate;
	private XmlSqlQuerySource queries = new XmlSqlQuerySource() ;
	
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

	@GetMapping("/cityprofiles/{state_name}")
	public ArrayList<CityProfile> getCityProfile(@PathVariable String state_name){
		state_name = state_name.substring(0, 1).toUpperCase() + state_name.substring(1).toLowerCase(); // make sure the first char is away capitalized 
		GetCityProfiles data = new GetCityProfiles(state_name);
		return data.getCityProfile();	
	}
	
	 @GetMapping("/cors-debug")
	    public String corsDebug() {
	        return "CORS Allowed Origin: " + corsAllowedOrigin;
	    }
}
