package com.razorpay.common.util;

import org.springframework.security.crypto.keygen.KeyGenerators;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomizerUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // usage:  Use CaseTokens, API Keys, URL links, configuration variables
    public static String randomBase64(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

}
