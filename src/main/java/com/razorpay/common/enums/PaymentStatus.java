package com.razorpay.common.enums;

public enum PaymentStatus {
    CREATED,
    AUTHORIZING,
    AUTHORIZED,
    CAPTURING,
    CAPTURED,
    FAILED,
    CANCELLED,
    EXPIRED,
    REFUNDING,
    REFUNDED,
    PARTIALLY_REFUNDED,
    SETTLED
}
