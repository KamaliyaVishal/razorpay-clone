package com.razorpay.merchant.dto.response;

import com.razorpay.common.enums.Environment;

import java.util.UUID;

public record CreateApiKeyResponse(
        UUID id,
        String keyId,
        String keySecretHash,
        Environment environment
) {}
