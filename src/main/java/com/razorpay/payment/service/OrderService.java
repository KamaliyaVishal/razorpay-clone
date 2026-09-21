package com.razorpay.payment.service;

import com.razorpay.payment.dto.request.CreateOrderRequest;
import com.razorpay.payment.dto.response.OrderResponse;
import com.razorpay.payment.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(UUID merchantId, CreateOrderRequest request);

    OrderResponse getOrderById(UUID merchantId, UUID orderId);

    OrderResponse cancelOrder(UUID merchantId, UUID orderId);

    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);
}
