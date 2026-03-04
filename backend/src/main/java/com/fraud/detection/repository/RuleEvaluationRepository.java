package com.fraud.detection.repository;

import com.fraud.detection.entity.RuleEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for RuleEvaluation entity.
 */
@Repository
public interface RuleEvaluationRepository extends JpaRepository<RuleEvaluation, UUID> {

    List<RuleEvaluation> findByTransactionId(UUID transactionId);

    List<RuleEvaluation> findByRuleId(UUID ruleId);

    List<RuleEvaluation> findByTransactionIdAndMatchedTrue(UUID transactionId);

    @Query("SELECT COUNT(re) FROM RuleEvaluation re WHERE re.ruleId = :ruleId AND re.matched = true")
    Long countMatchesByRuleId(@Param("ruleId") UUID ruleId);
}
