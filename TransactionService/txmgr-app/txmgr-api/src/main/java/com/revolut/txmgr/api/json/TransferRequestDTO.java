package com.revolut.txmgr.api.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonPropertyOrder({"transactionId", "sourceAccountId", "targetAccountId", "amount", "remarks"})
public class TransferRequestDTO {//extends ValidatableDTO{
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = -3484458197151739153L;

	//private static final String JSON_SCHEMA_NAME = "TransferRequestDTO.json";
	
	 /**
     * id
     */
    @JsonProperty(value = "transactionId", required = true)
    private final String transactionId;

    @JsonProperty(required = true)
    private final long sourceAccountId;

    @JsonProperty(required = true)
    private final long targetAccountId;

    @JsonProperty(required = true)
    private final long amount;
    
    @JsonProperty(required = false)
    private final String remarks;
    
    @JsonCreator
	public TransferRequestDTO(
			final @JsonProperty("transactionId") String transactionId,
			final @JsonProperty("sourceAccountId") long sourceAccountId,
			final @JsonProperty("targetAccountId") long targetAccountId,
			final @JsonProperty("amount") long amount,
			final @JsonProperty("remarks") String remarks
			) {

		//super(JSON_SCHEMA_NAME);
		this.transactionId = transactionId;
		this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.remarks = remarks;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public long getSourceAccountId() {
		return sourceAccountId;
	}

	public long getTargetAccountId() {
		return targetAccountId;
	}

	public long getAmount() {
		return amount;
	}

	public String getRemarks() {
		return remarks;
	}

}
