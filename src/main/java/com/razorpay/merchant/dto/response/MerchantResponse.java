package com.razorpay.merchant.dto.response;

import com.razorpay.common.enums.BusinessType;
import com.razorpay.common.enums.MerchantStatus;
import com.razorpay.merchant.entity.Merchant;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
    public static MerchantResponse fromEntity(Merchant merchant) {
        if (merchant == null) return null;

        return new MerchantResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getEmail(),
                merchant.getBusinessName(),
                merchant.getBusinessType(),
                merchant.getStatus()
        );
    }
}
