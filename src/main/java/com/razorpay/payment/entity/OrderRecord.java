package com.razorpay.payment.entity;

import com.razorpay.common.entity.Money;
import com.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.SQLType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_record")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    // No foreign key relationship to Merchant entity, cross-service reference by merchantId
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status = OrderStatus.CREATED;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @JdbcTypeCode((SqlTypes.JSON))
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, String> metadata;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

}
