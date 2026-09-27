package com.razorpay.operations.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Set<UUID> pollDue(int limit) {
        long now = getTime(LocalDateTime.now());

        Set<ZSetOperations.TypedTuple<String>> due = redisTemplate
                .opsForZSet().rangeByScoreWithScores(key, 0, now, 0, limit);

        if (due == null || due.isEmpty()) return Set.of();

        due.forEach(tuple -> redisTemplate.opsForZSet()
                .remove(key, tuple.getValue()));

        return due.stream()
                .map(tuple -> UUID.fromString(tuple.getValue()))
                .collect(Collectors.toSet());
    }

    public void enqueueIfAbsent(UUID id, LocalDateTime nextRetryAt) {
        redisTemplate.opsForZSet().addIfAbsent(key, id.toString(), getTime(nextRetryAt));
    }
}




















