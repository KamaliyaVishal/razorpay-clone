package com.razorpay.merchant.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchantWebhookConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "target_url", nullable = false, length = 500 )
    private String targetUrl;

    @Column(name = "webhook_secret_hash", nullable = false, length = 255)
    private String webhookSecretHash;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "event_types")
    private String eventTypes; // Comma-separated list of event types

}
