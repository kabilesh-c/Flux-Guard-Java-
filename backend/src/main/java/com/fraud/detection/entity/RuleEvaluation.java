package com.fraud.detection.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * RuleEvaluation entity representing the audit trail of rule evaluations.
 */
@Entity
@Table(name = "rule_evaluations", indexes = {
    @Index(name = "idx_rule_eval_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_rule_eval_rule_id", columnList = "rule_id"),
    @Index(name = "idx_rule_eval_matched", columnList = "matched"),
    @Index(name = "idx_rule_eval_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(nullable = false)
    private Boolean matched;

    @Column(columnDefinition = "jsonb")
    private String detail;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
