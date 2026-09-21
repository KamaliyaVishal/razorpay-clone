package com.razorpay.payment.payment_gateway;

import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;

public interface PaymentAdapter {
    PaymentResponse initiatePayment(PaymentRequest paymentRequest);
}
