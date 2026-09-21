package com.razorpay.payment.payment_gateway.adapter;

import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.payment_gateway.PaymentAdapter;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;

public class CardPaymentAdapter implements PaymentAdapter {
    @Override
    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        return null;
    }
}
