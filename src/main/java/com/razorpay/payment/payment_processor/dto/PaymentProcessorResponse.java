package com.razorpay.payment.payment_processor.dto;

public sealed interface PaymentProcessorResponse permits
        PaymentProcessorResponse.Pending,
        PaymentProcessorResponse.Success,
        PaymentProcessorResponse.Failure {

    record Pending(
            String processorReference
    ) implements PaymentProcessorResponse {
    }

    record Success(
            String processorReference,
            String bankReference
    ) implements PaymentProcessorResponse {
    }

    record Failure(
            String errorCode,
            String errorDescription
    ) implements PaymentProcessorResponse {
    }
}
