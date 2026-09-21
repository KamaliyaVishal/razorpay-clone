package com.razorpay.payment.payment_processor.strategy;

import com.razorpay.payment.payment_processor.PaymentProcessor;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorResponse;

public class CardPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
