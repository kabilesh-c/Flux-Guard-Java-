package com.fraud.detection.rules;

import com.fraud.detection.entity.Rule;
import com.fraud.detection.entity.Transaction;
import com.fraud.detection.entity.TransactionFeatures;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OPTIMIZED Core rule evaluation engine using Spring Expression Language (SpEL).
 * Features: Compiled expression caching, parallel evaluation support.
 */
@Slf4j
@Component
public class RuleEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();
    
    // Cache compiled expressions to avoid re-parsing (thread-safe)
    private final ConcurrentHashMap<String, Expression> expressionCache = new ConcurrentHashMap<>();

    /**
     * Evaluate a single rule against a transaction with its features.
     * Now uses cached compiled expressions for better performance.
     *
     * @param rule The fraud detection rule
     * @param transaction The transaction to evaluate
     * @param features Computed features for the transaction
     * @return EvaluationResult containing match status and details
     */
    public EvaluationResult evaluate(Rule rule, Transaction transaction, TransactionFeatures features) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Create evaluation context with transaction and feature data
            StandardEvaluationContext context = createEvaluationContext(transaction, features);
            
            // Get cached compiled expression or parse and cache new one
            Expression expression = expressionCache.computeIfAbsent(
                rule.getId().toString(), 
                k -> parser.parseExpression(rule.getExpression())
            );
            
            // Evaluate the cached expression
            Boolean matched = expression.getValue(context, Boolean.class);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (Boolean.TRUE.equals(matched)) {
                log.debug("Rule '{}' matched for transaction '{}'", 
                        rule.getName(), transaction.getTransactionId());
                
                return EvaluationResult.builder()
                        .matched(true)
                        .ruleId(rule.getId())
                        .ruleName(rule.getName())
                        .weight(rule.getWeight())
                        .severity(rule.getSeverity())
                        .action(rule.getAction())
                        .reason(generateReason(rule, transaction, features))
                        .executionTimeMs((int) executionTime)
                        .build();
            } else {
                return EvaluationResult.builder()
                        .matched(false)
                        .ruleId(rule.getId())
                        .ruleName(rule.getName())
                        .executionTimeMs((int) executionTime)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Error evaluating rule '{}' for transaction '{}': {}", 
                    rule.getName(), transaction.getTransactionId(), e.getMessage());
            
            return EvaluationResult.builder()
                    .matched(false)
                    .ruleId(rule.getId())
                    .ruleName(rule.getName())
                    .error(e.getMessage())
                    .executionTimeMs((int) (System.currentTimeMillis() - startTime))
                    .build();
        }
    }

    /**
     * Create SpEL evaluation context with transaction and feature variables.
     */
    private StandardEvaluationContext createEvaluationContext(Transaction transaction, TransactionFeatures features) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        // Transaction fields
        context.setVariable("amount", transaction.getAmount().doubleValue());
        context.setVariable("currency", transaction.getCurrency());
        context.setVariable("user_id", transaction.getUserId());
        context.setVariable("ip_address", transaction.getIpAddress());
        context.setVariable("device_fingerprint", transaction.getDeviceFingerprint());
        
        // Feature fields
        if (features != null) {
            context.setVariable("txn_count_last_1h", features.getTxnCountLast1h());
            context.setVariable("txn_count_last_24h", features.getTxnCountLast24h());
            context.setVariable("txn_count_last_7d", features.getTxnCountLast7d());
            context.setVariable("avg_amount_last_24h", 
                    features.getAvgAmountLast24h() != null ? features.getAvgAmountLast24h().doubleValue() : 0.0);
            context.setVariable("max_amount_last_24h", 
                    features.getMaxAmountLast24h() != null ? features.getMaxAmountLast24h().doubleValue() : 0.0);
            context.setVariable("unique_devices_last_24h", features.getUniqueDevicesLast24h());
            context.setVariable("unique_ips_last_24h", features.getUniqueIpsLast24h());
            context.setVariable("is_new_device", features.getIsNewDevice());
            context.setVariable("is_new_ip", features.getIsNewIp());
            context.setVariable("is_unusual_location", features.getIsUnusualLocation());
            context.setVariable("time_since_last_txn_minutes", features.getTimeSinceLastTxnMinutes());
        }
        
        return context;
    }

    /**
     * Generate human-readable reason for rule match.
     */
    private String generateReason(Rule rule, Transaction transaction, TransactionFeatures features) {
        Map<String, Object> reasonData = new HashMap<>();
        reasonData.put("rule", rule.getName());
        reasonData.put("amount", transaction.getAmount());
        reasonData.put("currency", transaction.getCurrency());
        
        if (features != null) {
            if (features.getTxnCountLast1h() != null && features.getTxnCountLast1h() > 0) {
                reasonData.put("txn_count_1h", features.getTxnCountLast1h());
            }
            if (features.getTxnCountLast24h() != null && features.getTxnCountLast24h() > 0) {
                reasonData.put("txn_count_24h", features.getTxnCountLast24h());
            }
            if (Boolean.TRUE.equals(features.getIsNewDevice())) {
                reasonData.put("new_device", true);
            }
            if (Boolean.TRUE.equals(features.getIsNewIp())) {
                reasonData.put("new_ip", true);
            }
            if (Boolean.TRUE.equals(features.getIsUnusualLocation())) {
                reasonData.put("unusual_location", true);
            }
        }
        
        return String.format("Rule '%s' triggered: %s", rule.getName(), reasonData);
    }
}
