package com.razorpay.merchant.dto.response;

import com.razorpay.common.enums.BusinessType;
import com.razorpay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus status
) {}
