package com.fraud.detection.repository;

import com.fraud.detection.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Alert entity.
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    Page<Alert> findByStatus(Alert.AlertStatus status, Pageable pageable);

    Page<Alert> findByLevel(Alert.AlertLevel level, Pageable pageable);

    Page<Alert> findByAssignedTo(UUID assignedTo, Pageable pageable);

    @Query("SELECT a FROM Alert a WHERE a.status = :status AND a.createdAt >= :since")
    List<Alert> findByStatusAndCreatedAtAfter(
            @Param("status") Alert.AlertStatus status,
            @Param("since") Instant since
    );

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.status = com.fraud.detection.entity.Alert$AlertStatus.NEW")
    Long countUnreadAlerts();

    List<Alert> findByTransactionId(UUID transactionId);
}
