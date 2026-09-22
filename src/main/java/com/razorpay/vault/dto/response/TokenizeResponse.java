package com.razorpay.vault.dto.response;


public record TokenizeResponse(
        String token,
        String lastFour,
        CardType brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
