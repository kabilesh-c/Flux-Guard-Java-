package com.fraud.detection.repository;

import com.fraud.detection.entity.Rule;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Rule entity with caching support.
 */
@Repository
public interface RuleRepository extends JpaRepository<Rule, UUID> {

    Optional<Rule> findByRuleId(String ruleId);

    /**
     * Find all active rules - cached for performance.
     */
    @Cacheable(value = "rules", key = "'active'")
    List<Rule> findByActiveTrue();

    List<Rule> findBySeverity(Rule.RuleSeverity severity);

    List<Rule> findByActiveAndSeverity(Boolean active, Rule.RuleSeverity severity);
}
