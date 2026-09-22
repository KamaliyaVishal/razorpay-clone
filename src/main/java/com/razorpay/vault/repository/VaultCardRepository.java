package com.razorpay.vault.repository;

import com.razorpay.vault.entity.VaultCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaultCardRepository extends JpaRepository<VaultCard, Long> {
}