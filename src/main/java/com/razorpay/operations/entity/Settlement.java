package com.razorpay.operations.entity;


import com.razorpay.common.entity.BaseEntity;
import com.razorpay.common.entity.Money;
import com.razorpay.common.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlements")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SettlementStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "gross_amount_unit", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "gross_amount_currency"))
    })
    private Money grossAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "refund_amount_unit")),
            @AttributeOverride(name = "currency", column = @Column(name = "refund_amount_currency"))
    })
    private Money refundAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "fee_amount_unit")),
            @AttributeOverride(name = "currency", column = @Column(name = "fee_amount_currency"))
    })
    private Money feeAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "tax_amount_unit")),
            @AttributeOverride(name = "currency", column = @Column(name = "tax_amount_currency"))
    })
    private Money taxAmount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "net_amount_unit", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "net_amount_currency"))
    })
    private Money netAmount;

    @Column(name = "bank_reference")
    private String bankReference;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

}
