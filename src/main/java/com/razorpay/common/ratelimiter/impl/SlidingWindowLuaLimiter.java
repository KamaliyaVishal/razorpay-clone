package com.razorpay.common.ratelimiter.impl;

import com.razorpay.common.ratelimiter.RateLimitResult;
import com.razorpay.common.ratelimiter.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding-lua")
public class SlidingWindowLuaLimiter implements RateLimiter {

    private final StringRedisTemplate redis;

    /**
     * Atomic sliding window via Lua.
     * KEYS[1] = redis key
     * ARGV[1] = now in milliseconds
     * ARGV[2] = window floor in milliseconds (now - windowMs)
     * ARGV[3] = max requests
     * ARGV[4] = window TTL in seconds (for EXPIRE)
     * ARGV[5] = unique member id for this request
     * <p>
     * Returns a three-element array:
     * [0] = 1 (allowed) or 0 (denied)
     * [1] = remaining count (maxRequests - count after add, or 0 if denied)
     * [2] = score of the oldest member (used to compute Retry-After), or 0 if denied on empty set
     */
    private static final String SLIDING_WINDOW_LUA = """
            local key      = KEYS[1]
            local now      = tonumber(ARGV[1])
            local floor    = tonumber(ARGV[2])
            local limit    = tonumber(ARGV[3])
            local ttl      = tonumber(ARGV[4])
            local member   = ARGV[5]
            
            -- prune expired members
            redis.call('ZREMRANGEBYSCORE', key, '-inf', floor)
            
            -- count current members
            local count = redis.call('ZCARD', key)
            
            if count >= limit then
                -- return oldest member score so caller can compute Retry-After
                local oldest = redis.call('ZRANGE', key, 0, 0, 'WITHSCORES')
                local oldestScore = 0
                if #oldest > 0 then
                    oldestScore = tonumber(oldest[2])
                end
                return {0, 0, oldestScore}
            end
            
            -- add this request and reset TTL atomically
            redis.call('ZADD', key, now, member)
            redis.call('EXPIRE', key, ttl)
            
            local remaining = limit - count - 1
            return {1, remaining, 0}
            """;

    private final RedisScript<List<Long>> script = RedisScript.of(SLIDING_WINDOW_LUA,
            (Class<List<Long>>) (Class<?>) List.class);

    @Override
    public RateLimitResult check(String key, int maxRequests, long retryAfterSeconds) {
        try {
            long nowMs = System.currentTimeMillis();
            long floorMs = nowMs - retryAfterSeconds * 1000;
            String member = UUID.randomUUID().toString();

            List<Long> result = redis.execute(
                    script,
                    List.of("ratelimit:sliding:" + key),
                    String.valueOf(nowMs),
                    String.valueOf(floorMs),
                    String.valueOf(maxRequests),
                    String.valueOf(retryAfterSeconds + 1),
                    member
            );

            if (result == null || result.isEmpty()) {
                // Redis unavailable — fail open
                return RateLimitResult.allowed(maxRequests);
            }

            boolean allowed = result.get(0) == 1L;
            int remaining = result.get(1).intValue();
            long oldestScoreMs = result.get(2);

            if (!allowed) {
                int retryAfter = oldestScoreMs > 0
                        ? (int) Math.max(1, (oldestScoreMs + retryAfterSeconds * 1000 - nowMs) / 1000)
                        : (int) retryAfterSeconds;
                return RateLimitResult.denied(retryAfter);
            }

            return RateLimitResult.allowed(remaining);

        } catch (DataAccessException e) {
            log.warn("Rate limiter unavailable, failing open for key={}", key, e);
            return RateLimitResult.allowed(maxRequests);
        }
    }
}