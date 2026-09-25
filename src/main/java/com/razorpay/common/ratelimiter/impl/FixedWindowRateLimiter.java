package com.razorpay.common.ratelimiter.impl;

import com.razorpay.common.ratelimiter.RateLimitResult;
import com.razorpay.common.ratelimiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a Fixed Window Rate Limiter using Redis.
 * <p>
 * ALGORITHM CONCEPT:
 * Time is divided into continuous, fixed intervals (e.g., blocks of 60 seconds).
 * A counter tracks requests within the current window block. When a window expires,
 * the key is deleted by Redis, automatically resetting the counter for the next window.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
public class FixedWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate redisTemplate;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long retryAfterSeconds) {

        String redisKey = "rateLimit:fixed" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        // Fallback protection: If Redis fails to return a count, fail-safe by letting the request pass.
        if (count == null) return RateLimitResult.allowed(maxRequestAllowed);

        // Start the Fixed Window for first request for provided time frame
        if (count == 1) redisTemplate.expire(redisKey, Duration.ofSeconds(retryAfterSeconds));

        // Once the limit reached, return the result with TTL & retryAfter time
        if (count > maxRequestAllowed) {
            Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            int retryAfter = (ttl != null & ttl > 0) ? ttl.intValue() : (int) retryAfterSeconds;
            return RateLimitResult.denied(retryAfter);
        }

        // Return the Result with allowed request count for each success request
        return RateLimitResult.allowed((int) (maxRequestAllowed - count));
    }
}
