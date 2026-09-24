package com.razorpay.merchant.cache;

import com.razorpay.common.enums.Environment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ApiKeyCacheEntry(
        String keyId,
        String keySecretHash,
        String previousKeySecretHash,
        LocalDateTime gracePeriodExpiresAt,
        UUID merchantId,
        Environment environment,
        boolean enabled
) {

    public boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }

}
