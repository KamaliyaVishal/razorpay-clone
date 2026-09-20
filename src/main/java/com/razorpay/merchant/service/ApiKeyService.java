package com.razorpay.merchant.service;

import com.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.razorpay.merchant.dto.response.ApiKeyResponse;
import com.razorpay.merchant.dto.response.CreateApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    CreateApiKeyResponse create(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyResponse> fetchAllMerchantApiKeys(UUID merchantId);

    String revokeApiKeyByMerchantId(UUID merchantId, String keyId);
}
