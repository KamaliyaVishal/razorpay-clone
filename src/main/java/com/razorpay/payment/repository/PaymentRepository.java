package com.razorpay.payment.repository;

import com.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findAllByOrderRecord_Id(UUID orderId);

    Optional<Payment> findByMerchantIdAndId(UUID merchantId, UUID paymentId);
}