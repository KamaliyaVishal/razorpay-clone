package com.razorpay.payment.gateway.adapter;

import com.razorpay.payment.dto.response.PaymentResponse;
import com.razorpay.payment.gateway.PaymentAdapter;
import com.razorpay.payment.gateway.dto.PaymentRequest;

public class UpiPaymentAdapter implements PaymentAdapter {

    @Override
    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        return null;
    }
}
