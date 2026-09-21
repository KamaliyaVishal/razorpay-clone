package com.razorpay.payment.payment_gateway;

import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;
import com.razorpay.payment.payment_gateway.dto.PaymentResult;

public interface PaymentAdapter {
    PaymentResult initiatePayment(PaymentRequest paymentRequest);
}
