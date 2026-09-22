package com.razorpay.payment.service.Impl;

import com.razorpay.common.enums.OrderStatus;
import com.razorpay.common.enums.PaymentStatus;
import com.razorpay.common.exception.BusinessRuleViolationException;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.payment.dto.request.PaymentInitRequest;
import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.entity.OrderRecord;
import com.razorpay.payment.entity.Payment;
import com.razorpay.payment.mapper.GlobalPaymentMapper;
import com.razorpay.payment.payment_gateway.PaymentGatewayRouter;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;
import com.razorpay.payment.payment_gateway.dto.PaymentResult;
import com.razorpay.payment.repository.OrderRepository;
import com.razorpay.payment.repository.PaymentRepository;
import com.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final GlobalPaymentMapper mapper;

    @Override
    public PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest request) {

        //Validate the order before payment
        OrderRecord order = orderRepository.findByMerchantIdAndId(merchantId, request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("OrderId", request.orderId()));


        if (!Set.of(OrderStatus.CREATED, OrderStatus.ATTEMPTED).contains(order.getStatus()))
            throw new BusinessRuleViolationException("Order cannot accept payment in status" + order.getStatus(),
                    "OrderStatus", order.getStatus());

        //Payment attempt capture in DB
        order.setStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);

        Payment payment = Payment.builder()
                .orderRecord(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .status(PaymentStatus.CREATED)
                .paymentMethod(request.method())
                .methodDetails(request.methodDetails())
                .build();

        paymentRepository.save(payment);

        //Payment initialed
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentId(payment.getId())
                .orderId(order.getId())
                .merchantId(merchantId)
                .amount(order.getAmount())
                .paymentMethod(request.method())
                .methodDetails(request.methodDetails())
                .build();

        PaymentResult paymentResult = paymentGatewayRouter.routePaymentToDedicatedMethod(paymentRequest);

        switch (paymentResult) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success ->{

            }
        }

        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        return mapper.toPaymentResponse(payment);
    }
}



























