package com.razorpay.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateWebhookConfigRequest(

        @NotBlank(message = "Webhook URL is required")
        @Size(max = 500)
        @Pattern(regexp = "^https?://.+", message = "Webhook URL must be a valid http(s) URL")
        String targetUrl,

        // Comma-separated fine-grained event type names (e.g. "PAYMENT_STATUS_CHANGED,REFUND_CREATED").
        // Null/blank/"ALL" subscribes to every event type.
        @Size(max = 1000)
        String eventTypes
) {
}
