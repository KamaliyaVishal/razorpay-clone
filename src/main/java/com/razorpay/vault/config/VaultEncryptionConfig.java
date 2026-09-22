package com.razorpay.vault.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesGcmBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Configuration
public class VaultEncryptionConfig {

    @Value("${vault.master-key}")
    private String masterKey;

    public static BytesEncryptor panEncryptor(byte[] dek) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(dek, "AES");

        return AesGcmBytesEncryptor.withSecretKey(secretKeySpec)
                .ivGenerator(KeyGenerators.secureRandom(12)) // NIST recommendation for GCM
                .build();
    }

    @Bean
    public BytesEncryptor dekEncryptor() {

        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
        SecretKeySpec secretKeySpec = new SecretKeySpec(masterKeyBytes, "AES");

        return AesGcmBytesEncryptor.withSecretKey(secretKeySpec)
                .ivGenerator(KeyGenerators.secureRandom(12)) // NIST recommendation for GCM
                .build();
    }

}
