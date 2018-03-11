package com.revolut.txmgr.transfer;

import java.util.UUID;

import com.revolut.txmgr.api.ResponseCode;

public class Transaction {

    private final UUID transactionId;
    private ResponseCode responseCode;
    private final long sourceAccountId;
    private final long sourceAmount;
    private final Currency sourceCurrency;
    private final long targetAccountId;
    private final long targetAmount;
    private final Currency targetCurrency;
    private final String remarks;


    /**
     * 
     * @param transactionId
     * @param sourceAccountId
     * @param sourceAmount
     * @param sourceCurrency
     * @param targetAccountId
     * @param targetAmount
     * @param targetCurrency
     * @param remarks
     */
    public Transaction(UUID transactionId, 
                        long sourceAccountId, long sourceAmount, Currency sourceCurrency,
                        long targetAccountId, long targetAmount, Currency targetCurrency,
                        String remarks) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.sourceAmount = sourceAmount;
        this.sourceCurrency = sourceCurrency;
        this.targetAccountId = targetAccountId;
        this.targetAmount = targetAmount;
        this.targetCurrency = targetCurrency;
        this.remarks = remarks;
    }


	public ResponseCode getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

}
