package com.razorpay.operations.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "dlq_events")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DlqEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webhook_event_id")
    private WebhookEvent webhookEvent;

    @Column(name = "final_error", length = 1000)
    private String finalError;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(name = "moved_at")
    private LocalDateTime movedAt;

    @Column(name = "replayed_at")
    private LocalDateTime replayedAt;

}
