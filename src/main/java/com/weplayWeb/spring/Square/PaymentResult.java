package com.weplayWeb.spring.Square;


/**
 * PaymentResult is an object representing the response back to the front end.
 */
public class PaymentResult {

	  private final String status;
	   private final Object errors;
	   private String nonce;
	   private EmailResult emailResult; 

	   // Constructor for payment-only results (maintain backward compatibility)
	   public PaymentResult(String status, Object errors) {
	        this.status = status;
	        this.errors = errors;
	    }
	   
	   // New constructor that includes email result
	    public PaymentResult(String status, Object errors, EmailResult emailResult) {
	        this.status = status;
	        this.errors = errors;
	        this.emailResult = emailResult;
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

		public EmailResult getEmailResult() {
			return emailResult;
		}

		public void setEmailResult(EmailResult emailResult) {
			this.emailResult = emailResult;
		}
}


