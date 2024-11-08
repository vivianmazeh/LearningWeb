package com.weplayWeb.spring.Square;

public class CustomerResponse {
	
	
	  private String squareCustomerId;
	  private String message;
	  
	  CustomerResponse(String squareCustomerId, String message){
	
		  this.squareCustomerId = squareCustomerId;
		  this.message = message;
	  }
	  
				
		public String getSquareCustomerId() {
			return squareCustomerId;
		}
		
		public void setSquareCustomerId(String squareCustomerId) {
			this.squareCustomerId = squareCustomerId;
		}
		
		public String getMessage() {
			return message;
		}
		
		public void setMessage(String message) {
			this.message = message;
		}
	    
}
