package com.razorpay.vault.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesGcmBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;


@Configuration
public class VaultEncryptionConfig {

    public static BytesEncryptor panEncryptor(byte[] dek) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(dek, "AES");

        return AesGcmBytesEncryptor.withSecretKey(secretKeySpec)
                .ivGenerator(KeyGenerators.secureRandom(12)) // NIST recommendation for GCM
                .build();
    }

}
