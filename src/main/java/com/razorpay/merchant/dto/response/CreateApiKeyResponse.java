package com.razorpay.merchant.dto.response;

import com.razorpay.common.enums.Environment;
import com.razorpay.merchant.entity.ApiKey;

import java.util.UUID;

public record CreateApiKeyResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {

    public static CreateApiKeyResponse fromEntity(ApiKey apiKey) {
        if (apiKey == null) return null;

        return new CreateApiKeyResponse(
                apiKey.getId(),
                apiKey.getKeyId(),
                apiKey.getKeySecretHash(),
                apiKey.getEnvironment()
        );

    }
}
