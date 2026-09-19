package com.razorpay.vault.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vault_card")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaultCard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 4)
    private String last4Digits;

    @Column(nullable = false, length = 6)
    private Integer bin;

    @Column(nullable = false)
    private byte[] encryptedPan;

    @Column(nullable = false)
    private byte[] encryptedDek;

    @Column(nullable = false)
    private String cardType;

    @Column(nullable = false, length = 2)
    private Integer expiryMonth;

    @Column(nullable = false, length = 4)
    private Integer expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private LocalDateTime deletedAt;

}
