package com.weplayWeb.spring.Square;

public class EmailResult {

	   private final String status;
	    private final String message;

	    public EmailResult(String status, String message) {
	        this.status = status;
	        this.message = message;
	    }

	    public String getStatus() {
	        return status;
	    }

	    public String getMessage() {
	        return message;
	    }
}
