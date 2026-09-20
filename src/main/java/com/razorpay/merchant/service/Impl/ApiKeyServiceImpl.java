package com.razorpay.merchant.service.Impl;

import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.common.util.RandomizerUtil;
import com.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.razorpay.merchant.dto.response.CreateApiKeyResponse;
import com.razorpay.merchant.entity.ApiKey;
import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.repository.ApiKeyRepository;
import com.razorpay.merchant.repository.MerchantRepository;
import com.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
    public CreateApiKeyResponse create(UUID merchantId, CreateApiKeyRequest request) {

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));


        String keyId = String.join(
                "_",
                "rzp",
                request.environment().name().toLowerCase(),
                RandomizerUtil.randomBase64(24)
        );

        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(rawSecret)
                .environment(request.environment())
                .build();

        apiKeyRepository.save(apiKey);

        return CreateApiKeyResponse.fromEntity(apiKey);
    }
}
