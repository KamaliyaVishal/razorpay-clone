package com.razorpay.payment.payment_gateway;

import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;
import com.razorpay.payment.payment_gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {
    PaymentResult initiatePayment(PaymentRequest paymentRequest);

    PaymentResult capturePayment(UUID paymentId);
}
