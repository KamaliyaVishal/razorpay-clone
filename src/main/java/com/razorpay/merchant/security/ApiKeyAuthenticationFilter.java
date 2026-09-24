package com.razorpay.merchant.security;

import com.razorpay.merchant.entity.ApiKey;
import com.razorpay.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String BASIC_PREFIX = "Basic ";
    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming request inside ApiKeyAuthenticationFilter : {}", request.getRequestURI());

        try {
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith(BASIC_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String[] credential = decodeHeader(header);
            if (credential == null)
                throw new BadRequestException("Malformed API-KEY header");

            String keyId = credential[0];
            String keySecret = credential[1];

            ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                    .orElseThrow(() -> new BadRequestException("Invalid or missing API-KEY"));

            if (!apiKey.isEnabled() && !isSecretKeyValid(apiKey, keySecret))
                throw new BadRequestException("Invalid or missing API-KEY");

            var auth = new UsernamePasswordAuthenticationToken(keyId, null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ROLE"))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            merchantContext.setMerchantId(apiKey.getMerchant().getId());
            merchantContext.setKeyId(apiKey.getKeyId());

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private String[] decodeHeader(String header) {
        String encode = header.substring(BASIC_PREFIX.length());
        String decode = new String(Base64.getDecoder().decode(encode), StandardCharsets.UTF_8);
        int colonIndex = decode.indexOf(":");
        if (colonIndex < 1) return null;

        return new String[]{decode.substring(0, colonIndex), decode.substring(colonIndex + 1)};
    }

    private boolean isSecretKeyValid(ApiKey apiKey, String keySecret) {

        if (bCryptPasswordEncoder.matches(keySecret, apiKey.getKeySecretHash()))
            return true;

        return apiKey.isInGracePeriod()
                && apiKey.getPreviousKeySecretHash() != null
                && bCryptPasswordEncoder.matches(keySecret, apiKey.getPreviousKeySecretHash());
    }
}
