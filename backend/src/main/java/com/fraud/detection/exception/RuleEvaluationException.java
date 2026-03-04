package com.fraud.detection.exception;

/**
 * Exception thrown when rule evaluation fails critically.
 */
public class RuleEvaluationException extends RuntimeException {
    
    private final String ruleId;
    private final String transactionId;
    
    public RuleEvaluationException(String ruleId, String transactionId, String message) {
        super(String.format("Rule evaluation failed - Rule: %s, Transaction: %s - %s", 
                ruleId, transactionId, message));
        this.ruleId = ruleId;
        this.transactionId = transactionId;
    }
    
    public RuleEvaluationException(String ruleId, String transactionId, Throwable cause) {
        super(String.format("Rule evaluation failed - Rule: %s, Transaction: %s", 
                ruleId, transactionId), cause);
        this.ruleId = ruleId;
        this.transactionId = transactionId;
    }
    
    public String getRuleId() {
        return ruleId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
}
