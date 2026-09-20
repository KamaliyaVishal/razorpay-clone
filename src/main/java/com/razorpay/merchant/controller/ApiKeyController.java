package com.razorpay.merchant.controller;

import com.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.razorpay.merchant.dto.response.CreateApiKeyResponse;
import com.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/{merchantId}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ResponseEntity<CreateApiKeyResponse> create(@PathVariable UUID merchantId,
                                                       @RequestBody @Valid CreateApiKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(apiKeyService.create(merchantId, request));
    }
}
