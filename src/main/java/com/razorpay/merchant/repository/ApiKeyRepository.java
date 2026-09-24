package com.razorpay.merchant.repository;

import com.razorpay.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    List<ApiKey> findAllByMerchantId(UUID merchantId);

    Optional<ApiKey> findByMerchant_IdAndKeyId(UUID merchantId, String keyId);

    Optional<ApiKey> findByKeyId(String keyId);
}