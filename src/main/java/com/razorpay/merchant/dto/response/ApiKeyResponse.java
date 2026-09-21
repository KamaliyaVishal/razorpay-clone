package com.razorpay.merchant.dto.response;

import com.razorpay.common.enums.Environment;

import java.time.LocalDateTime;

public record ApiKeyResponse(
        String keyId,
        Environment environment,
        LocalDateTime lastUsedAt,
        LocalDateTime rotatedAt
) {}
