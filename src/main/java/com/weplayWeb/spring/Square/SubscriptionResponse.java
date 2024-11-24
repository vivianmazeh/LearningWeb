package com.weplayWeb.spring.Square;

public class SubscriptionResponse {

	 private String status;
	    private Object data;
	    private String nonce;
	    private String error;

	    public SubscriptionResponse(String status, Object data, String nonce) {
	        this.status = status;
	        this.data = data;
	        this.nonce = nonce;
	    }

	    // Getters and setters
	    public String getStatus() { return status; }
	    public void setStatus(String status) { this.status = status; }
	    
	    public Object getData() { return data; }
	    public void setData(Object data) { this.data = data; }
	    
	    public String getNonce() { return nonce; }
	    public void setNonce(String nonce) { this.nonce = nonce; }
	    
	    public String getError() { return error; }
	    public void setError(String error) { this.error = error; }
}
