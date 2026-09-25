package com.razorpay.merchant.repository;

import com.razorpay.merchant.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByMerchant_IdAndEmail(UUID merchantId, String email);
}
