package com.razorpay.operations.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SettlementPaymentId {

    private UUID settlementId;

    private UUID paymentId;

}
