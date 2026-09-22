package com.razorpay.payment.payment_gateway.adapter;

import com.razorpay.payment.payment_gateway.PaymentAdapter;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;
import com.razorpay.payment.payment_gateway.dto.PaymentResult;

import java.util.UUID;

public class CardPaymentAdapter implements PaymentAdapter {
    @Override
    public PaymentResult initiatePayment(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public PaymentResult capturePayment(UUID paymentId) {
        return null;
    }
}
