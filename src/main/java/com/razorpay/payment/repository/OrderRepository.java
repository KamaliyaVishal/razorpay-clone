package com.razorpay.payment.repository;

import com.razorpay.payment.entity.OrderRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {

    boolean existsByMerchantIdAndReceipt(UUID merchantId, String receipt);

    Optional<OrderRecord> findByMerchantIdAndId(UUID merchantId, UUID orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderRecord o WHERE o.id = :uuid AND o.merchantId = :merchantId")
    Optional<OrderRecord> findByMerchantIdAndIdForUpdate(UUID merchantId, UUID uuid);
}