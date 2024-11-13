package com.weplayWeb.spring.Square;

import java.util.List;

/**
 * PaymentResult is an object representing the response back to the front end.
 */
public class PaymentResult {

	  private final String status;
	   private final Object errors;
	   private String nonce;

	   public PaymentResult(String status, Object errors) {
	        this.status = status;
	        this.errors = errors;
	    }

	    public String getStatus() {
	        return status;
	    }

	    public Object getErrors() {
	        return errors;
	    }

	    public String getNonce() {
	        return nonce;
	    }

	    public void setNonce(String nonce) {
	        this.nonce = nonce;
	    }
}
