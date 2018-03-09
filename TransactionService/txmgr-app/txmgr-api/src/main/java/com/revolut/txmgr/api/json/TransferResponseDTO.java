package com.revolut.txmgr.api.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.revolut.txmgr.api.ResponseCode;

@JsonSerialize
public class TransferResponseDTO {
    /**
     * uuid
     */
    @JsonProperty(value = "transaction_id")
    private String transactionId;
    @JsonProperty(value = "response_code")
    private int responseCode;
    private String message;

    public TransferResponseDTO setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    @JsonIgnore
    public TransferResponseDTO setResponseCode(ResponseCode responseCode) {
        return setResponseCode(responseCode.code())
                .setMessage(responseCode.message());
    }

    public TransferResponseDTO setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public TransferResponseDTO setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "transactionId=" + transactionId +
                ", responseCode" + responseCode +
                ", message='" + message + '\'' +
                '}';
    }
}
