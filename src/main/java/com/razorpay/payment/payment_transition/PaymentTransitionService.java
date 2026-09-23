package com.razorpay.payment.payment_transition;

import com.razorpay.common.enums.PaymentActor;
import com.razorpay.common.enums.PaymentEvent;
import com.razorpay.common.enums.PaymentStatus;
import com.razorpay.payment.entity.Payment;
import com.razorpay.payment.entity.PaymentTransitionLog;
import com.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentStateMachine paymentStateMachine;
    private final PaymentTransitionLogRepository paymentTransitionLogRepository;

    @Transactional(rollbackFor = Exception.class)
    public PaymentStatus apply(Payment payment, PaymentEvent paymentEvent) {

        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), paymentEvent);

        PaymentTransitionLog paymentTransitionLog = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .paymentEvent(paymentEvent)
                .toStatus(next)
                .paymentActor(PaymentActor.SYSTEM)
                .occurrenceAt(LocalDateTime.now())
                .build();
        paymentTransitionLogRepository.save(paymentTransitionLog);
        payment.setStatus(next);

        return next;
    }

}
