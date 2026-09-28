package com.razorpay.operations.webhook;

import com.razorpay.common.enums.WebhookEventStatus;
import com.razorpay.operations.entity.WebhookEvent;
import com.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliverExecutor {

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;
    private final RestClient restClient;

    private static final List<Duration> BACKOFF = List.of(
            Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofMinutes(30),
            Duration.ofHours(2), Duration.ofHours(8), Duration.ofHours(24));

    private final int MAX_ATTEMPTS = 7;

    @Value("${webhook.delivery.signature-header:X-Razorpay-Signature}")
    private String signatureHeader;

    @Transactional(rollbackFor = Exception.class)
    public void deliver(UUID webhookEventId) {
        Optional<WebhookEvent> webhookEvent = webhookEventRepository.findById(webhookEventId);

        if (webhookEvent.isEmpty()) {
            log.warn("No webhook event found for this id: {}", webhookEventId);
            return;
        }

        WebhookEvent event = webhookEvent.get();

        if (event.getStatus() == WebhookEventStatus.DELIVERED || event.getStatus() == WebhookEventStatus.DEAD) {
            log.warn("Cannot deliver the event {} in status: {}", webhookEventId, event.getStatus());
            return;
        }

        event.setAttemptCount(event.getAttemptCount() + 1);
        event.setLastAttemptedAt(LocalDateTime.now());

        try {
            var response = restClient.post()
                    .uri(event.getTargetUrl())
                    .header(signatureHeader, event.getSignature())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "event", event.getEventType(), "payload", event.getPayload()
                    )).retrieve()
                    .toBodilessEntity();

            int statusCode = response.getStatusCode().value();
            event.setLastResponseCode(statusCode);

            if (response.getStatusCode().is2xxSuccessful()) {
                event.setStatus(WebhookEventStatus.DELIVERED);
                event.setDeliveredAt(LocalDateTime.now());

                webhookEventRepository.save(event);
                log.info("Successfully called the merchant for webhook event: {}", webhookEventId);
                return;
            }
            handleAttemptFailed(event, "HTTP" + statusCode);

        } catch (RestClientException e) {
            handleAttemptFailed(event, e.getMessage());
            log.error("Got RestClientException", e);
        }
    }

    private void handleAttemptFailed(WebhookEvent event, String error) {
        event.setLastResponseBody(error);

        if (event.getAttemptCount() >= MAX_ATTEMPTS) {
            event.setStatus(WebhookEventStatus.DEAD);
            // TODO: handle DLQ
            return;
        }

        Duration backoff = BACKOFF.get(event.getAttemptCount() - 1);
        LocalDateTime nextRetryAt = LocalDateTime.now().plus(backoff);
        event.setStatus(WebhookEventStatus.FAILED);
        event.setNextRetryAt(nextRetryAt);
        webhookEventRepository.save(event);

        webhookRetryQueue.enqueue(event.getId(), nextRetryAt);

        log.error("Handling attempt failed for webhook event {} with attempts: {}, Next Retry at: {}",
                event.getId(), event.getAttemptCount(), nextRetryAt);
    }

}
























