package com.razorpay.merchant.service;

import com.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.razorpay.merchant.dto.response.ApiKeyResponse;
import com.razorpay.merchant.dto.response.CreateApiKeyResponse;
import com.razorpay.merchant.dto.response.DeleteResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApiKeyService {
    CreateApiKeyResponse create(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyResponse> fetchAllApiKeys(UUID merchantId);

    DeleteResponse revokeApiKeyByMerchantId(UUID merchantId, String keyId);

    CreateApiKeyResponse rotateApiKeyByMerchantId(UUID merchantId, String keyId);
}
