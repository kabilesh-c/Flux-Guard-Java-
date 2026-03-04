package com.fraud.detection.repository;

import com.fraud.detection.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByTransactionId(String transactionId);

    Page<Transaction> findByStatus(Transaction.TransactionStatus status, Pageable pageable);

    Page<Transaction> findByUserId(String userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status AND t.createdAt >= :since")
    List<Transaction> findByStatusAndCreatedAtAfter(
            @Param("status") Transaction.TransactionStatus status,
            @Param("since") Instant since
    );

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.createdAt >= :since")
    Long countByUserIdAndCreatedAtAfter(@Param("userId") String userId, @Param("since") Instant since);

    @Query("SELECT AVG(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.createdAt >= :since")
    Double getAverageAmountByUserIdAndCreatedAtAfter(@Param("userId") String userId, @Param("since") Instant since);

    @Query("SELECT MAX(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.createdAt >= :since")
    Double getMaxAmountByUserIdAndCreatedAtAfter(@Param("userId") String userId, @Param("since") Instant since);

    @Query("SELECT COUNT(DISTINCT t.deviceFingerprint) FROM Transaction t WHERE t.userId = :userId AND t.createdAt >= :since")
    Long countUniqueDevicesByUserIdAndCreatedAtAfter(@Param("userId") String userId, @Param("since") Instant since);

    @Query("SELECT COUNT(DISTINCT t.ipAddress) FROM Transaction t WHERE t.userId = :userId AND t.createdAt >= :since")
    Long countUniqueIpsByUserIdAndCreatedAtAfter(@Param("userId") String userId, @Param("since") Instant since);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId ORDER BY t.createdAt DESC LIMIT 1")
    Optional<Transaction> findLatestByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.deviceFingerprint = :deviceFingerprint")
    Long countByUserIdAndDeviceFingerprint(@Param("userId") String userId, @Param("deviceFingerprint") String deviceFingerprint);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.ipAddress = :ipAddress")
    Long countByUserIdAndIpAddress(@Param("userId") String userId, @Param("ipAddress") String ipAddress);

    // Count by status
    Long countByStatus(Transaction.TransactionStatus status);

    /**
     * OPTIMIZED: Single query to fetch all transaction features at once.
     * Replaces 6+ separate queries with one aggregated query.
     */
    @Query(value = """
        SELECT 
            COUNT(*) FILTER (WHERE created_at > :oneHourAgo) as txn_count_1h,
            COUNT(*) FILTER (WHERE created_at > :oneDayAgo) as txn_count_24h,
            COUNT(*) FILTER (WHERE created_at > :sevenDaysAgo) as txn_count_7d,
            COALESCE(AVG(amount) FILTER (WHERE created_at > :oneDayAgo), 0) as avg_amount_24h,
            COALESCE(MAX(amount) FILTER (WHERE created_at > :oneDayAgo), 0) as max_amount_24h,
            COUNT(DISTINCT device_fingerprint) FILTER (WHERE created_at > :oneDayAgo) as unique_devices_24h,
            COUNT(DISTINCT ip_address) FILTER (WHERE created_at > :oneDayAgo) as unique_ips_24h
        FROM transactions 
        WHERE user_id = :userId
        """, nativeQuery = true)
    Object[] getAggregatedFeatures(
        @Param("userId") String userId,
        @Param("oneHourAgo") Instant oneHourAgo,
        @Param("oneDayAgo") Instant oneDayAgo,
        @Param("sevenDaysAgo") Instant sevenDaysAgo
    );
}
