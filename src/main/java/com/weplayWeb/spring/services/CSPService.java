package com.weplayWeb.spring.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Base64;


@Service
public class CSPService {

	   private static final int NONCE_LENGTH = 16;
	   private final SecureRandom secureRandom;
	   private static final Logger logger = LoggerFactory.getLogger(CSPService.class);
	   
	   
	    @Value("${square.environment}")
	    private String squareEnvironment;
	    
	    
	    public CSPService() {
	        this.secureRandom = new SecureRandom();
	        logger.info("CSPService initialized with default constructor");
	        
	    }

	    public String generateNonce() {
	        byte[] nonceBytes = new byte[NONCE_LENGTH];
	        secureRandom.nextBytes(nonceBytes);
	        return Base64.getEncoder().encodeToString(nonceBytes);
	    }

	    public String generateCSPHeader(String nonce) {
	    	
	    	  String squareCdnDomain = "PRODUCTION".equalsIgnoreCase(squareEnvironment)
	    	            ? "web.squarecdn.com"
	    	            : "sandbox.web.squarecdn.com";
	    	           
	    	 
	    	  return String.format("default-src 'self'; " +
	    	            "style-src 'self' 'unsafe-inline' 'nonce-%s' https://%s; " +
	    	            "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://%s; " +
	    	            "frame-src 'self' https://%s; " +
	    	            "img-src 'self' data: https:; " +
	    	            "connect-src 'self' " +
	    	            "https://api.square.com " +
	    	            "https://sandbox.api.square.com " +
	    	            "https://csp-report.browser-intake-datadoghq.com; " +
	    	            "font-src 'self' data:; " +
	    	            "report-uri https://csp-report.browser-intake-datadoghq.com/api/v2/logs; " +
	    	            "report-to default",
	    	            nonce,
	    	            squareCdnDomain,
	    	            squareCdnDomain,
	    	            squareCdnDomain
	    	        );
	    }

	    
}
