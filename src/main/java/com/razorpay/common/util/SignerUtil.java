package com.razorpay.common.util;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Component
public class SignerUtil {

    private static final String ALGO = "HmacSHA256";

    public String sign(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance(ALGO);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGO));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("HmacSHA256 signing failed", e);
        }
    }
}