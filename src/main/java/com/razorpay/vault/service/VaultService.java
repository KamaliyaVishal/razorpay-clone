package com.razorpay.vault.service;

import com.razorpay.common.entity.Money;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorResponse;
import com.razorpay.vault.dto.request.TokenizeRequest;
import com.razorpay.vault.dto.response.TokenizeResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public interface VaultService {

    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID uuid, String token, Money amount, Map<String, Object> stringObjectMap);
}
