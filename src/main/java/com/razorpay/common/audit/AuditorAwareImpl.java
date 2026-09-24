package com.razorpay.common.audit;

import com.razorpay.merchant.security.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("AuditorAwareImpl")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final MerchantContext merchantContext;

    @Override
    public Optional<String> getCurrentAuditor() {

        String keyId = merchantContext.getKeyId();
        if (keyId != null && !keyId.isBlank()) return Optional.of(keyId);

        if (merchantContext.getMerchantId() != null)
            return Optional.of(merchantContext.getMerchantId().toString());

        return Optional.of("SYSTEM");
    }
}
