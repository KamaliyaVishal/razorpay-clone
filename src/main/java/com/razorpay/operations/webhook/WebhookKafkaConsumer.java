package com.razorpay.operations.webhook;

import com.razorpay.common.dto.WebhookTarget;
import com.razorpay.common.enums.WebhookEventStatus;
import com.razorpay.common.util.SignerUtil;
import com.razorpay.merchant.api.MerchantLookupService;
import com.razorpay.operations.entity.WebhookEvent;
import com.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookKafkaConsumer {

    private final MerchantLookupService merchantLookupService;
    private final ObjectMapper objectMapper;
    private final SignerUtil signerUtil;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;

    @KafkaListener(topics = {
            "${app.kafka.topics.payments:payments.events}",
            "${app.kafka.topics.orders:orders.events}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String, Object>> record, Acknowledgment ack) {
        try {
            // see the OutboxPoller where we define these fields and send  to kafka
            Map<String, Object> envelope = record.value();
            Map<String, Object> data = (Map<String, Object>) envelope.get("data");
            String eventType = (String) envelope.get("eventType");

            Object merchantIdRaw = data.get("merchantId");
            if (merchantIdRaw == null) {
                log.warn("No merchantId was found, skipping event: {}", eventType);
                ack.acknowledge();
                return;
            }

            UUID merchantId = UUID.fromString(merchantIdRaw.toString());

            List<WebhookTarget> targets = merchantLookupService.getActiveConfigsForEvent(merchantId, eventType);
            if (targets.isEmpty()) {
                log.debug("No webhook target was found, skipping event: {}", eventType);
                ack.acknowledge();
                return;
            }

            Map<String, Object> signatureData = Map.of("event", eventType, "payload", data);
            String signatureJson = objectMapper.writeValueAsString(signatureData);

            for (WebhookTarget target : targets) {
                String signature = signerUtil.sign(signatureJson, target.webhookSecret());

                WebhookEvent webhookEvent = WebhookEvent.builder()
                        .merchantId(merchantId)
                        .eventType(eventType)
                        .payload(data)
                        .targetUrl(target.targetUrl())
                        .signature(signature)
                        .status(WebhookEventStatus.PENDING)
                        .nextRetryAt(LocalDateTime.now())
                        .build();

                webhookEvent = webhookEventRepository.save(webhookEvent);

                /* Offloads webhook processing to a Redis queue instead of making a direct synchronous HTTP call to avoid synchronous HTTP overhead.
                     This design addresses three critical concurrency concerns:
                     1. Mitigates HTTP socket/connection pool exhaustion during high-traffic peak events (e.g., flash sales).
                     2. Shortens database transaction lifecycles by removing external network latency from the execution thread.
                     3. Prevents thread blocking and reduces system overhead under heavy concurrent request volumes.
                */
                webhookRetryQueue.enqueue(webhookEvent.getId(), webhookEvent.getNextRetryAt());

                log.info("Created a webhook event with id: {}", webhookEvent.getId());
            }
        } catch (Exception e) {
            log.error("Webhook consumer failed due to logical error, Could not process the record, offset: {}", record.offset(), e);
        }
    }

}



























