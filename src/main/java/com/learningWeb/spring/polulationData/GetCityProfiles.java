package com.learningWeb.spring.polulationData;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.learningWeb.spring.model.CityProfile;

public class GetCityProfiles {

	private static final String API_KEY = "941ef484004daa4c7a8738126f025504afcdb990";
    public static final String year = "2022";
	private static final String BASE_URL = "https://api.census.gov/data/2022/acs/acs5";
	public static final int MIN_POPULATION = 2500; // ONLY SHOW THE CITIES THAT HAVE MORE THAN 3500 POPULATION
    public String state_code;

   
    
    public GetCityProfiles(String state_name) {
    	  	state_code = StateCodeConverter.getStateCode(state_name);
    }
    
    public ArrayList<CityProfile> getCityProfile() {
    	/*
    	 * Total Population: B01003_001E
			Population Under 10: B01001_003E (Male under 5) + B01001_004E (Male 5 to 9) + 
			B01001_027E (Female under 5) + B01001_028E (Female 5 to 9)
			
			Median Household Income: B19013_001E
			Employment Rate: B23025_004E (Employed) / B23025_003E (Labor Force)
			Race Information: B02001_001E (Total), B02001_002E (White), B02001_003E (Black), B02001_005E (Asian), etc.
			Housing Units: B25001_001E
			Poverty Rate: B17001_002E (Below poverty level) and B17001_001E (Total population for poverty)
    	 * 
    	 * */
        try {
        	 String url = BASE_URL + "?get=NAME,B01003_001E,B01001_003E,B01001_004E,B01001_027E,"
        	 		+ "B01001_028E,B19013_001E,B23025_004E,B23025_003E,B02001_001E,B02001_002E,"
        	 		+ "B02001_003E,B02001_005E,B25001_001E,B17001_002E,B17001_001E&for=place:*&in=state:"
        	 		+ state_code + "&key=" + API_KEY;
	        String response = sendGetRequest(url);
	       return parseAndDisplayResponse(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return null;
        
    }
    private static String sendGetRequest(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    private static ArrayList<CityProfile> parseAndDisplayResponse(String response) throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(response);
        ArrayList<CityProfile> city_profile_list = new ArrayList<>();
        

        for (int i = 1; i < jsonArray.size(); i++) { // Start from 1 to skip headers
        	
        	CityProfile city_profile = new CityProfile();
            JSONArray cityData = (JSONArray) jsonArray.get(i);         
         
            city_profile.setCity_name((String) cityData.get(0));
            city_profile.setTotal_population(Integer.parseInt((String) cityData.get(1))); 
            
            int population_under_10 = Integer.parseInt((String) cityData.get(2)) + 
					            	  Integer.parseInt((String) cityData.get(3)) + 
					            	  Integer.parseInt((String) cityData.get(4)) + 
					            	  Integer.parseInt((String) cityData.get(5));
            
            city_profile.setUnder_10(population_under_10);
                      
            int medianHouseholdIncome = Integer.parseInt((String) cityData.get(6));
            city_profile.setMedian_household_income(medianHouseholdIncome);
            
            int employed = Integer.parseInt((String) cityData.get(7));
            int laborForce = Integer.parseInt((String) cityData.get(8));
            double employmentRate = (double) employed / laborForce * 100;
            city_profile.setEmployment_rate(Math.round(employmentRate * 100.0) / 100.0);
            
            int totalRace = Integer.parseInt((String) cityData.get(9));
            city_profile.setTotal_race(totalRace);
            
            int white = Integer.parseInt((String) cityData.get(10));
            city_profile.setRace_white(white);
            
            int black = Integer.parseInt((String) cityData.get(11));
            city_profile.setRace_black(black);
            
            int asian = Integer.parseInt((String) cityData.get(12));
            city_profile.setRace_asian(asian);
            
            int housingUnits = Integer.parseInt((String) cityData.get(13));
            city_profile.setHouse_units(housingUnits);
            
            int belowPovertyLevel = Integer.parseInt((String) cityData.get(14));                   
            int totalPoverty = Integer.parseInt((String) cityData.get(15));
            double povertyRate = (double) belowPovertyLevel / totalPoverty * 100;
            city_profile.setPoverty_rate(povertyRate);
            
            if(city_profile.getTotal_population() >= MIN_POPULATION) {
            	city_profile_list.add(city_profile);
            }
           }           	
		return city_profile_list;

    }
}
