package com.razorpay.payment.entity;

import com.razorpay.common.entity.Money;
import jakarta.persistence.*;
import lombok.*;

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

    private UUID merchantId;

    @Embedded
    private Money amount;

}
