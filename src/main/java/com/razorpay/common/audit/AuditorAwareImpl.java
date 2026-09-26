package com.razorpay.common.audit;

import com.razorpay.merchant.security.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

@Component("AuditorAwareImpl")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final MerchantContext merchantContext;

    @Override
    public Optional<String> getCurrentAuditor() {

        // Check if an active HTTP request context exists for the executing thread
        if (RequestContextHolder.getRequestAttributes() == null) {
            return Optional.of("SYSTEM");
        }

        try {
            String keyId = merchantContext.getKeyId();
            if (keyId != null && !keyId.isBlank()) return Optional.of(keyId);

            if (merchantContext.getMerchantId() != null)
                return Optional.of(merchantContext.getMerchantId().toString());
        } catch (Exception e) {
            // Fallback catch block in case scope proxies present unexpected behavior
            return Optional.of("SYSTEM");
        }


        return Optional.of("SYSTEM");
    }
}
