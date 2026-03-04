package com.fraud.detection.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Rule entity representing fraud detection rules.
 */
@Entity
@Table(name = "rules", indexes = {
    @Index(name = "idx_rules_active", columnList = "active"),
    @Index(name = "idx_rules_severity", columnList = "severity"),
    @Index(name = "idx_rules_rule_id", columnList = "rule_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "rule_id", nullable = false, unique = true, length = 64)
    private String ruleId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String expression;

    @Builder.Default
    @Column(nullable = false)
    private Integer weight = 50;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RuleSeverity severity = RuleSeverity.MEDIUM;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RuleAction action = RuleAction.FLAG;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_by")
    private UUID createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum RuleSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum RuleAction {
        FLAG, REJECT, NOTIFY, BLOCK
    }
}
