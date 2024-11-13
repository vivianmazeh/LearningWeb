package com.weplayWeb.spring.Square;



/**
 * TokenWrapper is a model object representing the token received from the front end.
 */
public class TokenWrapper {

    private String sourceId;
    private String idempotencyKey;
    private AmountMoney amountMoney;
    private String customerId;
    private Customer customer;
    private String buyerEmailAddress;
  

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getIdempotencyKey() {
      return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
      this.idempotencyKey = idempotencyKey;
    }

	public AmountMoney getAmountMoney() {
		return amountMoney;
	}

	public void setAmountMoney(AmountMoney amountMoney) {
		this.amountMoney =amountMoney;
	}


	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getBuyerEmailAddress() {
		return buyerEmailAddress;
	}

	public void setBuyerEmailAddress(String buyerEmailAddress) {
		this.buyerEmailAddress = buyerEmailAddress;
	}


}
