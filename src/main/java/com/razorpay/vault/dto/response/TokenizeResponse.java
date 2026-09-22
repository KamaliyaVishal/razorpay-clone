package com.razorpay.vault.dto.response;


import com.razorpay.common.enums.CardType;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardType brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
