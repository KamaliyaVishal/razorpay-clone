package com.razorpay.payment.gateway;

import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.gateway.dto.PaymentRequest;

public interface PaymentAdapter {
    PaymentResponse initiatePayment(PaymentRequest paymentRequest);
}
