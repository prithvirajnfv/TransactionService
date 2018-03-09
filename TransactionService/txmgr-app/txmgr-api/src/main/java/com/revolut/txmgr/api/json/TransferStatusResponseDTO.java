package com.revolut.txmgr.api.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.revolut.txmgr.api.ResponseCode;

import java.util.UUID;

@JsonSerialize
public class TransferStatusResponseDTO {
    
    @JsonProperty(value = "transactionId")
    private String transactionId;

    private int responseCode;
    private String message;

    public TransferStatusResponseDTO setTransactionId(String transactionId) {
        this.transactionId = UUID.fromString(transactionId).toString();
        return this;
    }

    @JsonIgnore
    public TransferStatusResponseDTO setResponseCode(ResponseCode responseCode) {
        return setResponseCode(responseCode.code())
                .setMessage(responseCode.message());
    }

    public TransferStatusResponseDTO setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public TransferStatusResponseDTO setMessage(String message) {
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
        return "PaymentStatusResponse{" +
                "transactionId=" + transactionId +
                ", responseCode=" + responseCode +
                ", message='" + message + '\'' +
                '}';
    }
}
