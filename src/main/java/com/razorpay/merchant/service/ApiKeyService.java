package com.razorpay.merchant.service;

import com.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.razorpay.merchant.dto.response.CreateApiKeyResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface ApiKeyService {
    CreateApiKeyResponse create(UUID merchantId, CreateApiKeyRequest request);
}
