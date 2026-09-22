package com.razorpay.payment.payment_processor.strategy;

import com.razorpay.common.util.RandomizerUtil;
import com.razorpay.payment.payment_processor.PaymentProcessor;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorRequest;
import com.razorpay.payment.payment_processor.dto.PaymentProcessorResponse;

public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("bank").toString() : null;

        // simulation
        if (BANK_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("BANK_REJECTED",
                    "Banked rejected the transaction registration"
            );
        }

        String processorRef = "NBK_PROCESSOR_" + RandomizerUtil.randomBase64(16);

        String redirectRef = "http://REDIRECT_BANK.com/" + processorRef;

        return new PaymentProcessorResponse.Success(processorRef, redirectRef);
    }
}
