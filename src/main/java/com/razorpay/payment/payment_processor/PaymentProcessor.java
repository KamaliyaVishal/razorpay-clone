package com.razorpay.payment.payment_processor;

import com.razorpay.payment.payment_processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);

}
