package com.weplayWeb.spring.Square;

/**
 * TokenWrapper is a model object representing the token received from the front end.
 */
public class TokenWrapper {

    private String sourceId;
    private String idempotencyKey;
    private AmountMoney amountMoney;
  

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
}
