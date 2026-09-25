package com.razorpay.payment.service.Impl;

import com.razorpay.common.enums.OrderStatus;
import com.razorpay.common.exception.DuplicateResourceException;
import com.razorpay.common.exception.BusinessRuleViolationException;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.payment.dto.request.CreateOrderRequest;
import com.razorpay.payment.dto.response.OrderResponse;
import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.entity.OrderRecord;
import com.razorpay.payment.entity.Payment;
import com.razorpay.payment.mapper.GlobalPaymentMapper;
import com.razorpay.payment.repository.OrderRepository;
import com.razorpay.payment.repository.PaymentRepository;
import com.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final GlobalPaymentMapper mapper;

    @Value("${payment.order.default-order-expiry-minutes : 30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(UUID merchantId, CreateOrderRequest request) {

        if (request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt()))
            throw new DuplicateResourceException("Order with receipt already exists", "Receipt", request.receipt());

        OrderRecord order = OrderRecord.builder()
                .receipt(request.receipt())
                .amount(request.amount())
                .notes(request.notes())
                .merchantId(merchantId)
                .status(OrderStatus.CREATED)
                .expiredAt(request.expiresAt() != null
                        ? request.expiresAt()
                        : LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();

        orderRepository.save(order);

        return mapper.toOrderResponse(order);
    }

    private OrderRecord findOrderById(UUID merchantId, UUID orderId) {
        return orderRepository.findByMerchantIdAndId(merchantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderId", orderId));
    }

    @Override
    public OrderResponse getOrderById(UUID merchantId, UUID orderId) {
        return mapper.toOrderResponse(findOrderById(merchantId, orderId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse cancelOrder(UUID merchantId, UUID orderId) {

        OrderRecord orderRecord = findOrderById(merchantId, orderId);

        if (Set.of(OrderStatus.CANCELLED, OrderStatus.PAID).contains(orderRecord.getStatus()))
            throw new BusinessRuleViolationException("This order cannot be cancelled because it is already paid or cancelled",
                    "OrderStatus", orderRecord.getStatus());

        orderRecord.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(orderRecord);

        return mapper.toOrderResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {

        OrderRecord orderRecord = findOrderById(merchantId, orderId);

        List<Payment> payments = paymentRepository.findAllByOrderRecord_Id(orderId);

        return mapper.toPaymentResponseList(payments);
    }
}
