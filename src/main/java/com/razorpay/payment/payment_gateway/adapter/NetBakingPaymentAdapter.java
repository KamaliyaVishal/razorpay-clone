package com.razorpay.payment.payment_gateway.adapter;

import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.payment.payment_gateway.PaymentAdapter;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;
import com.razorpay.payment.payment_gateway.dto.PaymentResult;
import com.razorpay.payment.payment_processor.PaymentProcessorRouter;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NetBakingPaymentAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiatePayment(PaymentRequest request) {

        log.info("Initiate request with NetBanking payment with PaymentId:{}", request.paymentId());

        PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                request.paymentId(),
                PaymentMethod.NETBANKING,
                request.amount(),
                request.methodDetails()
        );

        PaymentProcessorResponse paymentProcessorResponse =
                paymentProcessorRouter.routeToDedicatedPaymentProcessor(paymentProcessorRequest);

        return switch (paymentProcessorResponse) {
            case PaymentProcessorResponse.Pending pending ->
                    new PaymentResult.Pending(pending.processorReference());
            case PaymentProcessorResponse.Failure failure ->
                    new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Success success ->
                    new PaymentResult.Success(success.bankReference());
        };
    }
}