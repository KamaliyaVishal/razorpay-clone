package com.razorpay.payment.config;

import com.razorpay.common.enums.PaymentMethod;
import com.razorpay.payment.payment_processor.PaymentProcessor;
import com.razorpay.payment.payment_processor.strategy.CardPaymentProcessor;
import com.razorpay.payment.payment_processor.strategy.NetBankingPaymentProcessor;
import com.razorpay.payment.payment_processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UpiPaymentProcessor()
        );
    }
}
