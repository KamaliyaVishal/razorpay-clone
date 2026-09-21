package com.razorpay.merchant.entity;

import com.razorpay.common.entity.BaseEntity;
import com.razorpay.common.enums.Environment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_key", indexes = {
        @Index(name = "idx_api_key_merchant_env", columnList = "merchant_id, environment, enabled")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "key_id", nullable = false, unique = true, length = 50)
    private String keyId;

    @Column(name = "key_secret_hash", nullable = false, length = 100)
    private String keySecretHash;

    @Column(name = "previous_key_secret_hash", length = 100)
    private String previousKeySecretHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment", nullable = false)
    private Environment environment;

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "rotated_at")
    private LocalDateTime rotatedAt;

    @Column(name = "grace_period_expired_at")
    private LocalDateTime gracePeriodExpiredAt;

}
