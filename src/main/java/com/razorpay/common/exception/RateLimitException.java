package com.razorpay.common.exception;

import lombok.Getter;

@Getter
public class RateLimitException extends RuntimeException {
    private final int retryAfterSeconds;
    private final int remaining;

    public RateLimitException(String message, int retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
        this.remaining = 0;
    }
}
