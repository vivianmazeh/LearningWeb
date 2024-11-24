package com.weplayWeb.spring.Square;

public class SubscriptionRequest {

	 private String customerId;
	    private String cardId;
	    private String planId;
	    private int numberOfChildren;
	    private String startDate;

	    // Getters and setters
	    public String getCustomerId() { return customerId; }
	    public void setCustomerId(String customerId) { this.customerId = customerId; }
	    
	    public String getCardId() { return cardId; }
	    public void setCardId(String cardId) { this.cardId = cardId; }
	    
	    public String getPlanId() { return planId; }
	    public void setPlanId(String planId) { this.planId = planId; }
	    
	    public int getNumberOfChildren() { return numberOfChildren; }
	    public void setNumberOfChildren(int numberOfChildren) { this.numberOfChildren = numberOfChildren; }
	    
	    public String getStartDate() { return startDate; }
	    public void setStartDate(String startDate) { this.startDate = startDate; }
	
}
