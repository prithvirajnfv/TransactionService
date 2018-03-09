package com.revolut.txmgr.api;

/**
 * Response Status codes.<br/>
 * Code series:
 * <pre>
 * 0xx - success
 * 1xx - failures (non-final)
 * 2xx - two phase
 * </pre>
 */
public enum ResponseCode {
    // success
    SUCCESS(1, "Success"),
    
    //failures
    NOT_FOUND(100, "Transaction not found"),
    INVALID_SOURCE_ACCOUNT(101, "Source account does not exist"),
    INVALID_TARGET_ACCOUNT(102, "Target account does not exist"),
    
    LOCKED_SOURCE_ACCOUNT(103, "Source account is locked"),
    LOCKED_TARGET_ACCOUNT(104, "Target account is locked"),
    INSUFFICIENT_BALANCE(105, "Not enough money on source account");

    // twophase: waiting for confirm; reserved
  

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return "ResponseCode" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
