package com.razorpay.payment.controller;

import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.security.MerchantContext;
import com.razorpay.payment.dto.request.CreateOrderRequest;
import com.razorpay.payment.dto.response.OrderResponse;
import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(merchantContext.getMerchantId(), request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> fetchOrder(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrderById(merchantContext.getMerchantId(), orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.cancelOrder(merchantContext.getMerchantId(), orderId));
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> fetchAllPayments(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.listPayments(merchantContext.getMerchantId(), orderId));
    }

}
