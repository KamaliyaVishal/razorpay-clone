package com.razorpay.vault.service.impl;

import com.razorpay.common.entity.Money;
import com.razorpay.common.enums.CardType;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.common.util.RandomizerUtil;
import com.razorpay.payment.payment_processor.PaymentProcessorRouter;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorResponse;
import com.razorpay.vault.config.VaultEncryptionConfig;
import com.razorpay.vault.dto.request.TokenizeRequest;
import com.razorpay.vault.dto.response.TokenizeResponse;
import com.razorpay.vault.entity.CardToken;
import com.razorpay.vault.entity.VaultCard;
import com.razorpay.vault.repository.CardTokenRepository;
import com.razorpay.vault.repository.VaultCardRepository;
import com.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VaultServiceImpl implements VaultService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {

        String pan = request.pan();
        String lastFour = pan.substring(pan.length() - 4);
        String bin = pan.substring(0, 6);
        CardType cardType = detectCardType(pan);

        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultEncryptionConfig.panEncryptor(dek)
                .encrypt(pan.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek = dekEncryptor.encrypt(dek);

        VaultCard vaultCard = VaultCard.builder()
                .bin(Integer.parseInt(bin))
                .last4Digits(lastFour)
                .cardHolderName(request.cardHolderName())
                .encryptedDek(encryptedDek)
                .encryptedPan(encryptedPan)
                .cardType(cardType)
                .expiryMonth(request.expiryMonth())
                .expiryYear(request.expiryYear())
                .build();

        String randomToken = "tok_" + RandomizerUtil.randomBase64(32);

        CardToken cardToken = CardToken.builder()
                .vaultCard(vaultCard)
                .merchantId(merchantId)
                .token(randomToken)
                .customerId(request.customerId())
                .build();

        cardTokenRepository.save(cardToken);
        return new TokenizeResponse(randomToken, lastFour, cardType, request.expiryMonth(), request.expiryYear());
    }

    @Override
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails) {

        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token", token));

        VaultCard vaultCard = cardToken.getVaultCard();
        byte[] panBytes = null;

        try {
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());

            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest
                    .card(paymentId, pan, expiry, amount, methodDetails);

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter
                    .routeToDedicatedPaymentProcessor(paymentProcessorRequest);

            log.info("Vault charge registered with token: {}*****", token.substring(0, 4));

            return paymentProcessorResponse;

        } catch (Exception e) {
            log.warn("Vault charge failed with token: {}*****", token.substring(0, 4));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());

        } finally {
            if (panBytes != null) Arrays.fill(panBytes, (byte) 0);
        }
    }

    private CardType detectCardType(String pan) {

        if (pan == null || pan.isBlank()) return CardType.UNKNOWN;

        // Industry Standard: Strip all spaces and hyphens before running regex checks
        String sanitizedPan = pan.replaceAll("[\\s-]", "");

        // Loop through enum values to locate match pattern
        for (CardType type : CardType.values()) {
            if (type.getPattern() != null && type.getPattern().matcher(sanitizedPan).matches()) {
                return type;
            }
        }

        return CardType.UNKNOWN;
    }

}
