package com.razorpay.merchant.dto.request;

import com.razorpay.common.enums.Environment;
import jakarta.validation.constraints.NotNull;

public record CreateApiKeyRequest(

        @NotNull(message = "Environment cannot be null")
        Environment environment
) {
}
