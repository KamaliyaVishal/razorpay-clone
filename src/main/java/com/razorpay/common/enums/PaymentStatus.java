package com.razorpay.common.enums;

public enum PaymentStatus {
    CREATED,
    AUTHORIZING,
    AUTHORIZED,
    CAPTURING,
    CAPTURED,
    FAILED,
    CANCELLED,
    AUTH_EXPIRED,
    REFUNDED,
    PARTIALLY_REFUNDED,
    SETTLED
}
