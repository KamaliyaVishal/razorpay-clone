package com.razorpay.operations.repository;

import com.razorpay.common.enums.WebhookEventStatus;
import com.razorpay.operations.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    List<WebhookEvent> findByStatusAndNextRetryAtBefore(WebhookEventStatus webhookEventStatus, LocalDateTime now);
}