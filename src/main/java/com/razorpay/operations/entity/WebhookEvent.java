package com.razorpay.operations.entity;

import com.razorpay.common.enums.WebhookEventStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "webhook_events")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(name = "signature", nullable = false)
    private String signature;

    @Column(name = "status", nullable = false)
    private WebhookEventStatus status = WebhookEventStatus.PENDING;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(name = "last_attempted_at")
    private LocalDateTime lastAttemptedAt;

    @Column(name = "last_response_code")
    private Integer lastResponseCode;

    @Column(name = "last_response_body")
    private String lastResponseBody;

}
