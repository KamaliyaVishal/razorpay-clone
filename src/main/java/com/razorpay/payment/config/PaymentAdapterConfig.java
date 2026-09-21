package com.razorpay.payment.config;

import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.payment.gateway.PaymentAdapter;
import com.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.razorpay.payment.gateway.adapter.NetBakingPaymentAdapter;
import com.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.NETBANKING, new NetBakingPaymentAdapter(),
                PaymentMethod.UPI, new UpiPaymentAdapter()
        );
    }
}
