package com.razorpay.common.ratelimiter.impl;

import com.razorpay.common.ratelimiter.RateLimitResult;
import com.razorpay.common.ratelimiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Token bucket rate limiter: capacity == maxRequests, refill rate == maxRequests / windowSeconds
 * tokens/sec. Unlike the fixed window, tokens trickle back continuously instead of resetting all
 * at once at a window boundary, so it doesn't allow a burst of 2x maxRequests around the edge of
 * two windows. State (tokens remaining, last refill timestamp) lives in a Redis hash; refill +
 * consume is done atomically in a single Lua script so two concurrent requests for the same key
 * can't both read the same "tokens available" snapshot and both be allowed through.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "bucket")
public class TokenBucketRateLimiter implements RateLimiter {

    private static final RedisScript<List> SCRIPT = new DefaultRedisScript<>("""
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refillPerSec = tonumber(ARGV[2])
            local nowMs = tonumber(ARGV[3])
            local ttlSeconds = tonumber(ARGV[4])
            
            local data = redis.call('HMGET', key, 'tokens', 'ts')
            local tokens = tonumber(data[1])
            local lastTs = tonumber(data[2])
            
            if tokens == nil then
                tokens = capacity
                lastTs = nowMs
            end
            
            local elapsedSec = math.max(0, (nowMs - lastTs) / 1000)
            tokens = math.min(capacity, tokens + elapsedSec * refillPerSec)
            
            local allowed = 0
            if tokens >= 1 then
                tokens = tokens - 1
                allowed = 1
            end
            
            redis.call('HMSET', key, 'tokens', tokens, 'ts', nowMs)
            redis.call('EXPIRE', key, ttlSeconds)
            
            local retryAfter = 0
            if allowed == 0 then
                retryAfter = math.ceil((1 - tokens) / refillPerSec)
            end
            
            return {allowed, math.floor(tokens), retryAfter}
            """, List.class);

    private final StringRedisTemplate redis;

    @Override
    @SuppressWarnings("unchecked")
    public RateLimitResult check(String key, int maxRequests, long windowSeconds) {
        try {
            String redisKey = "ratelimit:bucket:" + key;
            double refillPerSec = (double) maxRequests / windowSeconds;
            // keep bucket state around for two windows of inactivity before Redis reclaims it
            long ttlSeconds = windowSeconds * 2;

            List<Long> result = redis.execute(SCRIPT,
                    List.of(redisKey),
                    String.valueOf(maxRequests),
                    String.valueOf(refillPerSec),
                    String.valueOf(System.currentTimeMillis()),
                    String.valueOf(ttlSeconds));

            boolean allowed = result.get(0) == 1L;
            int remaining = result.get(1).intValue();
            int retryAfter = result.get(2).intValue();

            return allowed ? RateLimitResult.allowed(remaining) : RateLimitResult.denied(Math.max(1, retryAfter));
        } catch (DataAccessException e) {
            log.warn("Rate limiter unavailable, failing open for key={}", key, e);
            return RateLimitResult.allowed(maxRequests);
        }
    }
}
