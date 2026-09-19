package com.razorpay.payment.entity;

import com.razorpay.common.enums.PaymentEvent;
import com.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transition_log")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransitionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false)
    private PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private PaymentStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_event", nullable = false)
    private PaymentEvent paymentEvent;

    @Column(name = "actor", nullable = false, length = 50)
    private String actor; // The actor who performed the transition, e.g., "system", "user", etc.

    @Column(name = "occurrence_at", nullable = false)
    private LocalDateTime occurrenceAt; // The timestamp when the transition occurred

}
