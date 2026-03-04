package com.fraud.detection.repository;

import com.fraud.detection.entity.TransactionFeatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for TransactionFeatures entity.
 */
@Repository
public interface TransactionFeaturesRepository extends JpaRepository<TransactionFeatures, UUID> {

    Optional<TransactionFeatures> findByTransactionId(UUID transactionId);
}
