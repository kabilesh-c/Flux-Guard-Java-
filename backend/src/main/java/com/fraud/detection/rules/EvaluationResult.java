package com.fraud.detection.rules;

import com.fraud.detection.entity.Rule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Result of a rule evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {
    private UUID ruleId;
    private String ruleName;
    private Boolean matched;
    private Integer weight;
    private Rule.RuleSeverity severity;
    private Rule.RuleAction action;
    private String reason;
    private Integer executionTimeMs;
    private String error;
}
