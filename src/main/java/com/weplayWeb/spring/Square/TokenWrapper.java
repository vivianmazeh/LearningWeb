package com.weplayWeb.spring.Square;

import java.util.ArrayList;
import java.util.List;

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

    
    public record Order(double price, int quantityOfOrder, String sectionName, boolean isMembership, int numberOfChildrenAllowed  ) {}
    
    List<Order>orderInfo = new ArrayList<Order>();

    public List<Order> getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(List<Order> orderInfo) {
		this.orderInfo = orderInfo;
	}

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