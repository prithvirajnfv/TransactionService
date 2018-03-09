package com.revolut.txmgr.transfer;

import java.util.UUID;

import com.revolut.txmgr.api.ResponseCode;

public class Transaction {

    private final UUID transactionId;
    /**
     * for now, it's always success. Reserved for two-phase payments.
     */
    private final ResponseCode responseCode;
    private final long sourceAccountId;
    /**
     * minor units
     */
    private final long sourceAmount;
    private final Currency sourceCurrency;

    private final long targetAccountId;
    /**
     * minor units
     */
    private final long targetAmount;
    private final Currency targetCurrency;

    private final String comment;


    /**
     * 
     * @param transactionId
     * @param sourceAccountId
     * @param sourceAmount
     * @param sourceCurrency
     * @param targetAccountId
     * @param targetAmount
     * @param targetCurrency
     * @param comment
     */
    public Transaction(UUID transactionId, ResponseCode responseCode,
                        long sourceAccountId, long sourceAmount, Currency sourceCurrency,
                        long targetAccountId, long targetAmount, Currency targetCurrency,
                        String comment) {
        this.transactionId = transactionId;
        this.responseCode = responseCode;
        this.sourceAccountId = sourceAccountId;
        this.sourceAmount = sourceAmount;
        this.sourceCurrency = sourceCurrency;
        this.targetAccountId = targetAccountId;
        this.targetAmount = targetAmount;
        this.targetCurrency = targetCurrency;
        this.comment = comment;
    }


	public ResponseCode getResponseCode() {
		return responseCode;
	}

}
