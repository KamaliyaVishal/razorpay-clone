package com.razorpay.payment.payment_gateway.adapter;

import com.razorpay.payment.payment_gateway.PaymentAdapter;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;
import com.razorpay.payment.payment_gateway.dto.PaymentResult;
import com.razorpay.payment.payment_processor.PaymentProcessor;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorResponse;
import com.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
    public PaymentResult initiatePayment(PaymentRequest request) {

        String token = request.methodDetails().get("token").toString();

        PaymentProcessorResponse response = vaultService
                .charge(request.paymentId(), token, request.amount(), request.methodDetails());

        return switch (response) {
            case PaymentProcessorResponse.Success success
                    -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure ->
                    new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending
                    -> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capturePayment(UUID paymentId) {
        return null;
    }
}
