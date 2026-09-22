package com.razorpay.vault.service.impl;

import com.razorpay.common.enums.CardType;
import com.razorpay.vault.dto.request.TokenizeRequest;
import com.razorpay.vault.dto.response.TokenizeResponse;
import com.razorpay.vault.repository.CardTokenRepository;
import com.razorpay.vault.repository.VaultCardRepository;
import com.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final VaultCardRepository vaultCardRepository;
    private final CardTokenRepository cardTokenRepository;

    @Override
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {

        String pan = request.pan();
        String lastFour = pan.substring(pan.length() - 4);
        String bin = pan.substring(0, 6);
        CardType cardType = detectCardType(pan);


        return null;
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
