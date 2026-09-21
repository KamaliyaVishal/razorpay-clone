package com.razorpay.merchant.entity;

import com.razorpay.common.entity.BaseEntity;
import com.razorpay.common.enums.BusinessType;
import com.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant", indexes = {
        @Index(name = "idx_merchant_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 200)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private BusinessType businessType;

    @Column(length = 200)
    private String websiteURL;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(length = 20)
    private String gstID;

    @Column(length = 20)
    private String panID;

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 20)
    private String settlementBankIFSC;

    @Column(length = 200)
    private String settlementBankAccountHolderName;

}
