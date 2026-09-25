package com.razorpay.payment.service.Impl;

import com.razorpay.common.enums.OrderStatus;
import com.razorpay.common.enums.PaymentEvent;
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
import com.razorpay.payment.payment_transition.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final GlobalPaymentMapper mapper;
    private final PaymentTransitionService paymentTransitionService;

    @Override
    /**
     * Why do we explicitly write rollbackFor = Exception.class?
     * By default, Spring’s standard @Transactional annotation only rolls back for unchecked exceptions (subclasses of RuntimeException and Error, like NullPointerException or IllegalArgumentException).
     * It will not roll back your database if a checked exception occurs (subclasses of Exception that you are forced to catch or declare, such as IOException, SQLException, or custom business exceptions).
     * By writing rollbackFor = Exception.class, you change this behavior to be 100% bulletproof. It forces Spring to roll back the database for every single type of exception—both checked and unchecked.
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest request) {

        // Validate the order before payment
        //OrderRecord order = orderRepository.findByMerchantIdAndId(merchantId, request.orderId())
        //        .orElseThrow(() -> new ResourceNotFoundException("OrderId", request.orderId()));


        // @Lock(LockModeType.PESSIMISTIC_WRITE) : used to block concurrent updates on a specific database record.
        OrderRecord order = orderRepository.findByMerchantIdAndIdForUpdate(merchantId, request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("OrderId", request.orderId()));

        if (!Set.of(OrderStatus.CREATED, OrderStatus.ATTEMPTED).contains(order.getStatus()))
            throw new BusinessRuleViolationException("Order cannot accept payment in status " + order.getStatus(),
                    "OrderStatus", order.getStatus());

        // Payment attempt capture in DB
        order.setStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);

        Payment payment = Payment.builder()
                .orderRecord(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .status(PaymentStatus.CREATED)
                .paymentMethod(request.method())
                .methodDetails(request.methodDetails())
                .idempotencyKey(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment);

        // Payment initialed
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentId(payment.getId())
                .orderId(order.getId())
                .merchantId(merchantId)
                .amount(order.getAmount())
                .paymentMethod(request.method())
                .methodDetails(request.methodDetails())
                .build();

        // Payment states derived from state transitions
        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult paymentResult = paymentGatewayRouter
                .routeInitiatePaymentStrategy(paymentRequest);

        switch (paymentResult) {
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
                // Do not set payment states directly; use the state machine instead to prevent unintended state transitions.
                payment.setStatus(paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL));
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {
                log.warn("Invalid result state in initiate Payment!");
                return null;
            }
        }

        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        return mapper.toPaymentResponse(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse capturePayment(UUID merchantId, UUID paymentId) {

        //Payment payment = paymentRepository.findByMerchantIdAndId(merchantId, paymentId)
        //       .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        // @Lock(LockModeType.PESSIMISTIC_WRITE) : used to block concurrent updates on a specific database record.
        Payment payment = paymentRepository.findByMerchantIdAndIdForUpdate(merchantId, paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        // Do not set payment states directly; use the state machine instead to prevent unintended state transitions.
        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
        PaymentResult paymentResult = paymentGatewayRouter
                .routeCapturePaymentStrategy(payment.getPaymentMethod(), paymentId);

        switch (paymentResult) {
            case PaymentResult.Pending pending -> {

            }
            case PaymentResult.Failure failure -> {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
                log.info("Payment failed while capturing for Payment : {}", paymentId);
            }
            case PaymentResult.Success success -> {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                payment.setProcessorReference(success.bankReference());
                log.info("Payment captured for Payment : {}", paymentId);
            }
        }

        return null;
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef,
                                     String errorCode, String errorDescription) {

        //Payment payment = paymentRepository.findById(paymentId)
        //       .orElseThrow(() -> new ResourceNotFoundException("PaymentId", paymentId));

        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentId", paymentId));

        if (payment.getStatus() != PaymentStatus.AUTHORIZING) {
            log.warn("Payment is not in Authorizing state, paymentID: {}, status: {}", paymentId, payment.getStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrderRecord();

        if (approve) {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setCapturedAt(LocalDateTime.now());

            //Auto-Capture
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter
                    .routeCapturePaymentStrategy(payment.getPaymentMethod(), paymentId);

            switch (captureResult) {
                case PaymentResult.Success success -> {
                    paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                    payment.setCapturedAt(LocalDateTime.now());
                    orderRecord.setStatus(OrderStatus.PAID);
                }
                case PaymentResult.Failure failure -> {
                    paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                    payment.setErrorCode(failure.errorCode());
                    payment.setErrorDescription(failure.errorDescription());

                }
                default -> throw new IllegalStateException("Unexpected value: " + captureResult);
            }

        } else {
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);
    }
}



























