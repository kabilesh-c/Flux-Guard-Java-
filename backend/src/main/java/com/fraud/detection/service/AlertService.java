package com.fraud.detection.service;

import com.fraud.detection.entity.Alert;
import com.fraud.detection.entity.Transaction;
import com.fraud.detection.repository.AlertRepository;
import com.fraud.detection.rules.EvaluationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing alerts and real-time notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Create an alert for a flagged/rejected transaction.
     */
    public Alert createAlert(Transaction transaction, List<EvaluationResult> evaluationResults) {
        log.info("Creating alert for transaction: {}", transaction.getTransactionId());

        // Determine alert level based on risk score
        Alert.AlertLevel level = determineAlertLevel(transaction.getRiskScore());

        // Build alert message
        String message = buildAlertMessage(transaction, evaluationResults);

        Alert alert = Alert.builder()
                .transactionId(transaction.getId())
                .level(level)
                .title(String.format("%s Transaction Detected", level))
                .message(message)
                .status(Alert.AlertStatus.NEW)
                .build();

        alert = alertRepository.save(alert);

        // Send real-time notification via WebSocket
        sendRealtimeAlert(alert, transaction);

        return alert;
    }

    /**
     * Determine alert level from risk score.
     */
    private Alert.AlertLevel determineAlertLevel(Integer riskScore) {
        if (riskScore == null) {
            return Alert.AlertLevel.LOW;
        }
        if (riskScore >= 90) {
            return Alert.AlertLevel.CRITICAL;
        } else if (riskScore >= 80) {
            return Alert.AlertLevel.HIGH;
        } else if (riskScore >= 50) {
            return Alert.AlertLevel.MEDIUM;
        } else {
            return Alert.AlertLevel.LOW;
        }
    }

    /**
     * Build alert message with triggered rules.
     */
    private String buildAlertMessage(Transaction transaction, List<EvaluationResult> results) {
        List<String> triggeredRules = results.stream()
                .filter(r -> Boolean.TRUE.equals(r.getMatched()))
                .map(EvaluationResult::getRuleName)
                .collect(Collectors.toList());

        return String.format(
                "Transaction %s for %s %s has been %s. Risk score: %d. Triggered rules: %s",
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus(),
                transaction.getRiskScore(),
                String.join(", ", triggeredRules)
        );
    }

    /**
     * Send real-time alert via WebSocket.
     */
    private void sendRealtimeAlert(Alert alert, Transaction transaction) {
        try {
            // Create alert payload
            var payload = new AlertNotification(
                    alert.getId().toString(),
                    alert.getLevel().toString(),
                    alert.getTitle(),
                    alert.getMessage(),
                    transaction.getTransactionId(),
                    transaction.getRiskScore(),
                    alert.getCreatedAt().toString()
            );

            // Send to global alerts topic
            messagingTemplate.convertAndSend("/topic/alerts", payload);

            log.debug("Sent real-time alert for transaction: {}", transaction.getTransactionId());
        } catch (Exception e) {
            log.error("Error sending real-time alert: {}", e.getMessage());
        }
    }

    /**
     * Alert notification payload for WebSocket.
     */
    public record AlertNotification(
            String alertId,
            String level,
            String title,
            String message,
            String transactionId,
            Integer riskScore,
            String timestamp
    ) {}
}
