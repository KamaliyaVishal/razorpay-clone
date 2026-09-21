package com.razorpay.payment.payment_gateway;

import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.common.exception.BusinessRuleViolationException;
import com.razorpay.payment.payment_gateway.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapterMap;

    public void routePaymentToDedicatedMethod(PaymentRequest request) {
        PaymentAdapter paymentAdapter = paymentAdapterMap.get(request.paymentMethod());
        if (paymentAdapter == null)
            throw new BusinessRuleViolationException("No Payment gateway method register fot method" + request.paymentMethod(),
                    "Payment Method", request.paymentMethod());

        paymentAdapter.initiatePayment(request);
    }

}
