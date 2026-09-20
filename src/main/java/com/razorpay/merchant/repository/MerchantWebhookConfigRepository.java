package com.razorpay.merchant.repository;

import com.razorpay.merchant.entity.MerchantWebhookConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MerchantWebhookConfigRepository extends JpaRepository<MerchantWebhookConfig, UUID> {
}