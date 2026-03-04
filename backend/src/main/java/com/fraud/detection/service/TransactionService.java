package com.fraud.detection.service;

import com.fraud.detection.dto.TransactionRequest;
import com.fraud.detection.dto.TransactionResponse;
import com.fraud.detection.entity.Transaction;
import com.fraud.detection.exception.DuplicateTransactionException;
import com.fraud.detection.exception.ResourceNotFoundException;
import com.fraud.detection.repository.TransactionRepository;
import com.fraud.detection.rules.EvaluationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * ENHANCED Service for transaction management and ingestion with proper exception handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;
    private final ObjectMapper objectMapper;

    /**
     * Ingest a new transaction and evaluate it for fraud.
     */
    @Transactional
    public TransactionResponse ingestTransaction(TransactionRequest request) {
        log.info("Ingesting transaction: {}", request.getTransactionId());

        // Check for duplicate transaction ID
        if (transactionRepository.findByTransactionId(request.getTransactionId()).isPresent()) {
            throw new DuplicateTransactionException(request.getTransactionId());
        }

        // Create transaction entity
        Transaction transaction = Transaction.builder()
                .transactionId(request.getTransactionId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .sourceAccount(request.getSourceAccount())
                .destAccount(request.getDestAccount())
                .transactionType(request.getTransactionType() != null ? request.getTransactionType() : "TRANSFER")
                .ipAddress(request.getIpAddress())
                .deviceFingerprint(request.getDeviceFingerprint())
                .status(Transaction.TransactionStatus.PENDING)
                .riskScore(0)
                .build();

        // Serialize location and metadata to JSON
        try {
            if (request.getLocation() != null) {
                transaction.setLocation(objectMapper.writeValueAsString(request.getLocation()));
            }
            if (request.getMetadata() != null) {
                transaction.setMetadata(objectMapper.writeValueAsString(request.getMetadata()));
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing JSON fields: {}", e.getMessage());
        }

        // Save transaction
        transaction = transactionRepository.save(transaction);

        // Evaluate for fraud
        List<EvaluationResult> evaluations = fraudDetectionService.evaluateTransaction(transaction);

        // Build response
        return buildTransactionResponse(transaction, evaluations);
    }

    /**
     * Get transaction by ID.
     */
    public TransactionResponse getTransaction(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        return buildTransactionResponse(transaction, null);
    }

    /**
     * Get paginated list of transactions.
     */
    public Page<TransactionResponse> getTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(txn -> buildTransactionResponse(txn, null));
    }

    /**
     * Get transactions by status.
     */
    public Page<TransactionResponse> getTransactionsByStatus(Transaction.TransactionStatus status, Pageable pageable) {
        return transactionRepository.findByStatus(status, pageable)
                .map(txn -> buildTransactionResponse(txn, null));
    }

    /**
     * Retry evaluation for a transaction.
     */
    @Transactional
    public TransactionResponse retryTransaction(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        List<EvaluationResult> evaluations = fraudDetectionService.retryEvaluation(transaction);

        return buildTransactionResponse(transaction, evaluations);
    }

    /**
     * Reset transaction to pending state.
     */
    @Transactional
    public TransactionResponse resetTransaction(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", id));

        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setRiskScore(0);
        transaction.setEvaluatedAt(null);
        transactionRepository.save(transaction);

        return buildTransactionResponse(transaction, null);
    }

    /**
     * Build transaction response DTO.
     */
    private TransactionResponse buildTransactionResponse(Transaction transaction, List<EvaluationResult> evaluations) {
        TransactionResponse.TransactionResponseBuilder builder = TransactionResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUserId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .sourceAccount(transaction.getSourceAccount())
                .destAccount(transaction.getDestAccount())
                .transactionType(transaction.getTransactionType())
                .ipAddress(transaction.getIpAddress())
                .deviceFingerprint(transaction.getDeviceFingerprint())
                .status(transaction.getStatus().toString())
                .riskScore(transaction.getRiskScore())
                .evaluatedAt(transaction.getEvaluatedAt())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt());

        // Parse JSON fields
        try {
            if (transaction.getLocation() != null) {
                builder.location(objectMapper.readValue(transaction.getLocation(), objectMapper.getTypeFactory()
                        .constructMapType(java.util.HashMap.class, String.class, Object.class)));
            }
            if (transaction.getMetadata() != null) {
                builder.metadata(objectMapper.readValue(transaction.getMetadata(), objectMapper.getTypeFactory()
                        .constructMapType(java.util.HashMap.class, String.class, Object.class)));
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON fields: {}", e.getMessage());
        }

        // Add evaluation summaries if provided
        if (evaluations != null && !evaluations.isEmpty()) {
            List<TransactionResponse.RuleEvaluationSummary> summaries = evaluations.stream()
                    .filter(e -> Boolean.TRUE.equals(e.getMatched()))
                    .map(e -> TransactionResponse.RuleEvaluationSummary.builder()
                            .ruleId(e.getRuleId().toString())
                            .ruleName(e.getRuleName())
                            .matched(e.getMatched())
                            .weight(e.getWeight())
                            .reason(e.getReason())
                            .build())
                    .toList();
            builder.ruleEvaluations(summaries);
        }

        return builder.build();
    }
}
