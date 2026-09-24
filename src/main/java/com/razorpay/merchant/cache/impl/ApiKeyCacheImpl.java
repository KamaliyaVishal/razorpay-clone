package com.razorpay.merchant.cache.impl;

import com.razorpay.merchant.cache.ApiKeyCache;
import com.razorpay.merchant.cache.ApiKeyCacheEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyCacheImpl implements ApiKeyCache {

    private static final String PREFIX = "apikey:";
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${data.redis.ttl-duration:5}")
    private Long ttlDuration;

    @Override
    public Optional<ApiKeyCacheEntry> get(String keyId) {
        try {
            String json = stringRedisTemplate.opsForValue().get(PREFIX + keyId);
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, ApiKeyCacheEntry.class));
        } catch (Exception e) {
            log.warn("ApiKey cache read filed, keyId: {}", keyId);
            return Optional.empty();
        }
    }

    @Override
    public void put(String keyId, ApiKeyCacheEntry apiKeyCacheEntry) {
        try {
            stringRedisTemplate.opsForValue().set(
                    PREFIX + keyId,
                    objectMapper.writeValueAsString(apiKeyCacheEntry),
                    Duration.ofMinutes(ttlDuration));
        } catch (Exception e) {
            log.warn("ApiKey cache put failed, keyId: {}", keyId);
        }
    }

    @Override
    public void evict(String keyId) {
        stringRedisTemplate.delete(keyId);
    }
}
