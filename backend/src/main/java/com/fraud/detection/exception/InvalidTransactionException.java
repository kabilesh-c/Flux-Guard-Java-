package com.fraud.detection.exception;

/**
 * Exception thrown when transaction data is invalid or malformed.
 */
public class InvalidTransactionException extends RuntimeException {
    
    public InvalidTransactionException(String message) {
        super(message);
    }
    
    public InvalidTransactionException(String field, String reason) {
        super(String.format("Invalid transaction: field '%s' - %s", field, reason));
    }
}
