package com.razorpay.operations.repository;

import com.razorpay.operations.entity.SettlementPayment;
import com.razorpay.operations.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
}
