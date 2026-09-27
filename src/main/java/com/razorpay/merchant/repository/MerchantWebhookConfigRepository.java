package com.razorpay.merchant.repository;

import com.razorpay.merchant.entity.MerchantWebhookConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantWebhookConfigRepository extends JpaRepository<MerchantWebhookConfig, UUID> {

    List<MerchantWebhookConfig> findByMerchant_Id(UUID merchantId);

    Optional<MerchantWebhookConfig> findByIdAndMerchant_Id(UUID configId, UUID merchantId);

    List<MerchantWebhookConfig> findByMerchant_IdAndEnabledTrue(UUID merchantId);
}