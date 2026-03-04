package com.fraud.detection.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * TransactionFeatures entity representing computed features for fraud detection.
 */
@Entity
@Table(name = "transaction_features", indexes = {
    @Index(name = "idx_txn_features_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_txn_features_user_id", columnList = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFeatures {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Builder.Default
    @Column(name = "txn_count_last_1h")
    private Integer txnCountLast1h = 0;

    @Builder.Default
    @Column(name = "txn_count_last_24h")
    private Integer txnCountLast24h = 0;

    @Builder.Default
    @Column(name = "txn_count_last_7d")
    private Integer txnCountLast7d = 0;

    @Builder.Default
    @Column(name = "avg_amount_last_24h", precision = 12, scale = 2)
    private BigDecimal avgAmountLast24h = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "max_amount_last_24h", precision = 12, scale = 2)
    private BigDecimal maxAmountLast24h = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "unique_devices_last_24h")
    private Integer uniqueDevicesLast24h = 0;

    @Builder.Default
    @Column(name = "unique_ips_last_24h")
    private Integer uniqueIpsLast24h = 0;

    @Builder.Default
    @Column(name = "is_new_device")
    private Boolean isNewDevice = false;

    @Builder.Default
    @Column(name = "is_new_ip")
    private Boolean isNewIp = false;

    @Builder.Default
    @Column(name = "is_unusual_location")
    private Boolean isUnusualLocation = false;

    @Column(name = "time_since_last_txn_minutes")
    private Integer timeSinceLastTxnMinutes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
