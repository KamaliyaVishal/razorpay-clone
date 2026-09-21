package com.razorpay.payment.payment_gateway.dto;

import com.razorpay.common.entity.Money;
import com.razorpay.common.enums.PaymentMethod;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

@Builder
public record PaymentRequest(
        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails
) {
}