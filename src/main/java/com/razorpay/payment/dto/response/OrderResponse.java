package com.razorpay.payment.dto.response;

import com.razorpay.common.entity.Money;
import com.razorpay.common.enums.OrderStatus;
import com.razorpay.payment.entity.OrderRecord;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID merchantId,
        String receipt,
        Money amount,
        OrderStatus status,
        Integer attempts,
        Map<String, Object> notes,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
    public static OrderResponse fromEntity(OrderRecord order) {
        if (order == null) return null;

        return new OrderResponse(
                order.getId(),
                order.getMerchantId(),
                order.getReceipt(),
                order.getAmount(),
                order.getStatus(),
                order.getAttemptCount(),
                order.getNotes() != null
                        ? Map.copyOf(order.getNotes())
                        : Map.of(), // Ensures immutability
                order.getExpiredAt(),
                null
        );
    }
}
