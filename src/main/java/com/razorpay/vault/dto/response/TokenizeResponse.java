package com.razorpay.vault.dto.response;

import com.razorpay.common.enums.CardType;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardType cardType,
        Integer expiryMonth,
        Integer expiryYear
) {
}
