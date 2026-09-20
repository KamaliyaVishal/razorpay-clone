package com.razorpay.payment.service;

import com.razorpay.payment.dto.request.CreateOrderRequest;
import com.razorpay.payment.dto.response.OrderResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID merchantId, CreateOrderRequest request);
}
