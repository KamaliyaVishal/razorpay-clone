package com.razorpay.common.ratelimiter;

public interface RateLimiter {

    RateLimitResult check(String key, int maxRequestAllowed, long retryAfterSeconds);

}
