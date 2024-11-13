package com.weplayWeb.spring.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.weplayWeb.spring.services.CSPService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
@Order(1)
public class CSPFilter extends OncePerRequestFilter  {
	
	private static final Logger logger = LoggerFactory.getLogger(CSPFilter.class);
	  private final CSPService cspService;
	  
	  public CSPFilter(CSPService cspService) {
	        this.cspService = cspService;
	        logger.info("CSPFilter initialized with CSPService");
	    }

	  @Override 
	    protected void doFilterInternal(HttpServletRequest request,
	                                  HttpServletResponse response,
	                                  FilterChain filterChain)
	            throws ServletException, IOException {
		  try { 
			  if (shouldSkipFilter(request.getRequestURI())) {
		            filterChain.doFilter(request, response);
		            return;
		        }
	
		        String nonce = cspService.generateNonce();
	
		     // Set CSP header
		        response.setHeader("Content-Security-Policy", cspService.generateCSPHeader(nonce));
	
		        // Make nonce available to the response
		        response.setHeader("X-CSP-Nonce", nonce);
	
	
		        filterChain.doFilter(request, response);
		  } catch(Exception e) {
			  logger.error("Error in CSP filter", e);
	            filterChain.doFilter(request, response);
		  }
	    }
	  
	    private boolean shouldSkipFilter(String uri) {
	        return uri != null && (
	            uri.endsWith(".css") ||
	            uri.endsWith(".js") ||
	            uri.endsWith(".ico") ||
	            uri.endsWith(".png") ||
	            uri.endsWith(".jpg") ||
	            uri.endsWith(".svg")
	        );
	    }
	  
}
