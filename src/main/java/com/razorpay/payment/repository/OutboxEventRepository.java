package com.razorpay.payment.repository;

import com.razorpay.payment.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

}