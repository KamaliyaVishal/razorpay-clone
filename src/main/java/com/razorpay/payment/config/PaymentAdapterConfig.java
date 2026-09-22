package com.razorpay.payment.config;

import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.payment.payment_gateway.PaymentAdapter;
import com.razorpay.payment.payment_gateway.adapter.CardPaymentAdapter;
import com.razorpay.payment.payment_gateway.adapter.NetBakingPaymentAdapter;
import com.razorpay.payment.payment_gateway.adapter.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final CardPaymentAdapter cardPaymentAdapter;
    private final NetBakingPaymentAdapter netBakingPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.NETBANKING, netBakingPaymentAdapter,
                PaymentMethod.UPI, upiPaymentAdapter
        );
    }
}
