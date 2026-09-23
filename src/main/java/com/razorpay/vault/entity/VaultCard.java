package com.razorpay.vault.entity;

import com.razorpay.common.entity.BaseEntity;
import com.razorpay.common.enums.CardType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vault_card")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaultCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 4)
    private String last4Digits;

    @Column(nullable = false, length = 6)
    private Integer bin;

    @Column(nullable = false)
    private byte[] encryptedPan;

    @Column(nullable = false)
    private byte[] encryptedDek;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardType cardType;

    @Column(nullable = false, length = 2)
    private Integer expiryMonth;

    @Column(nullable = false, length = 4)
    private Integer expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private LocalDateTime deletedAt;

}
