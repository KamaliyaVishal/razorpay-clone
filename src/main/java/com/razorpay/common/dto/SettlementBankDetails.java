package com.razorpay.common.dto;

public record SettlementBankDetails(
        String accountNumber,
        String ifsc,
        String accountHolderName
) {
}
