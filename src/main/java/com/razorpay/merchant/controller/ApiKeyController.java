package com.razorpay.merchant.controller;

import com.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.razorpay.merchant.dto.response.ApiKeyResponse;
import com.razorpay.merchant.dto.response.CreateApiKeyResponse;
import com.razorpay.merchant.dto.response.DeleteResponse;
import com.razorpay.merchant.security.MerchantContext;
import com.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchants/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<CreateApiKeyResponse> create(@RequestBody @Valid CreateApiKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.create(merchantContext.getMerchantId(), request));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> fetchAllApiKeys() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.fetchAllApiKeys(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<DeleteResponse> revokeApiKey(@PathVariable String keyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.revokeApiKeyByMerchantId(merchantContext.getMerchantId(), keyId));
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<CreateApiKeyResponse> rotateApiKey(@PathVariable String keyId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.rotateApiKeyByMerchantId(merchantContext.getMerchantId(), keyId));
    }


}
