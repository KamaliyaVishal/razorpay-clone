package com.razorpay.operations.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookRetryQueue {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.webhook.delivery.redis-key:webhook-retry}")
    private String key;

    public void enqueue(UUID webhookEventId, LocalDateTime retryAt) {
        long time = getTime(retryAt);
        redisTemplate.opsForZSet().add(key, webhookEventId.toString(), time);
        log.info("Enqueued a webhook event with id: {}", webhookEventId);
    }

    private static long getTime(LocalDateTime retryAt) {
        return retryAt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}




















