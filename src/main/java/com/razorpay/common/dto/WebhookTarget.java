package com.razorpay.common.dto;

import java.util.UUID;

public record WebhookTarget(
        UUID configId,
        String targetUrl,
        String webhookSecret) {
}
