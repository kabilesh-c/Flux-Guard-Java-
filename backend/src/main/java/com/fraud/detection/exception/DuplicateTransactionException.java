package com.fraud.detection.exception;

/**
 * Exception thrown when attempting to create a transaction with a duplicate transaction ID.
 */
public class DuplicateTransactionException extends RuntimeException {
    
    private final String transactionId;
    
    public DuplicateTransactionException(String transactionId) {
        super(String.format("Transaction with ID '%s' already exists", transactionId));
        this.transactionId = transactionId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
}
