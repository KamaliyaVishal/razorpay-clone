package com.razorpay.merchant.service.Impl;

import com.razorpay.common.exception.BusinessRuleViolationException;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.common.util.RandomizerUtil;
import com.razorpay.merchant.cache.impl.ApiKeyCacheImpl;
import com.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.razorpay.merchant.dto.response.ApiKeyResponse;
import com.razorpay.merchant.dto.response.CreateApiKeyResponse;
import com.razorpay.merchant.dto.response.DeleteResponse;
import com.razorpay.merchant.entity.ApiKey;
import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.mapper.GlobalMerchantMapper;
import com.razorpay.merchant.repository.ApiKeyRepository;
import com.razorpay.merchant.repository.MerchantRepository;
import com.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;
    private final GlobalMerchantMapper mapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final ApiKeyCacheImpl apiKeyCache;

    @Value("${app.api-key.keyId-length: 24}")
    private Integer keyIdLength;

    @Value("${app.api-key.rawSecret-length: 40}")
    private Integer rawSecretLength;

    @Value("${app.api-key.gracePeriod-expiry-time-in-hour: 24}")
    private Integer gracePeriodTime;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateApiKeyResponse create(UUID merchantId, CreateApiKeyRequest request) {

        // Don't require @Lock(LockModeType.PESSIMISTIC_WRITE) as unique key constraints
        // won't let concurrent requests generate duplicate keys; instead, it will throw a Duplicate Resource exception.
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant", merchantId));

        String keyId = String.join(
                "_",
                "rzp",
                request.environment().name().toLowerCase(),
                RandomizerUtil.randomBase64(keyIdLength)
        );

        String rawSecret = RandomizerUtil.randomBase64(rawSecretLength);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(bCryptPasswordEncoder.encode(rawSecret))
                .environment(request.environment())
                .build();

        apiKeyRepository.save(apiKey);

        return mapper.toCreateApiKeyResponse(apiKey);
    }

    @Override
    public List<ApiKeyResponse> fetchAllApiKeys(UUID merchantId) {
        return mapper.toApiKeyResponseList(apiKeyRepository.findAllByMerchantId(merchantId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResponse revokeApiKeyByMerchantId(UUID merchantId, String keyId) {

        ApiKey apiKey = apiKeyRepository.findByMerchant_IdAndKeyId(merchantId, keyId)
                .orElseThrow(() -> new ResourceNotFoundException("API_Key", keyId));

        apiKey.setEnabled(false);

        // Redis cache evict on revoke key
        apiKeyCache.evict(apiKey.getKeyId());

        // Optional: apiKeyRepository.save(apiKey);
        // When this method ends, @Transactional commits,
        // dirty checking triggers, and the UPDATE SQL runs.
        apiKeyRepository.save(apiKey);

        return DeleteResponse.fromEntity(apiKey, keyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateApiKeyResponse rotateApiKeyByMerchantId(UUID merchantId, String keyId) {

        ApiKey apiKey = apiKeyRepository.findByMerchant_IdAndKeyId(merchantId, keyId)
                .orElseThrow(() -> new ResourceNotFoundException("API_Key", keyId));

        if (!apiKey.isEnabled())
            throw new BusinessRuleViolationException("Cannot rotate API key [%s] because it is disabled or revoked.".formatted(keyId),
                    "keyId", keyId);

        String newRawSecret = RandomizerUtil.randomBase64(rawSecretLength);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(bCryptPasswordEncoder.encode(newRawSecret));
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiredAt(LocalDateTime.now().plusHours(gracePeriodTime));

        // Redis cache evict on rotate key
        apiKeyCache.evict(apiKey.getKeyId());

        apiKeyRepository.save(apiKey);

        return mapper.toCreateApiKeyResponse(apiKey);
    }
}
