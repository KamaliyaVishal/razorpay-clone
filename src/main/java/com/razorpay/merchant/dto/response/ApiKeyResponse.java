package com.razorpay.merchant.dto.response;

import com.razorpay.common.enums.Environment;
import com.razorpay.merchant.entity.ApiKey;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyResponse(
        UUID merchantId,
        String keyId,
        Environment environment,
        LocalDateTime lastUsedAt,
        LocalDateTime rotatedAt
) {
    public static ApiKeyResponse fromEntity(ApiKey entity) {
        if (entity == null) return null;

        return new ApiKeyResponse(
                entity.getMerchant().getId(),
                entity.getKeyId(),
                entity.getEnvironment(),
                entity.getLastUsedAt(),
                entity.getRotatedAt()
        );
    }
}
