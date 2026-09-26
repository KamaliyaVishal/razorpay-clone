package com.razorpay.payment.outbox;

import com.razorpay.common.enums.OutboxStatus;
import com.razorpay.payment.entity.OutboxEvent;
import com.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxResultHandler {

    private final OutboxEventRepository outboxEventRepository;
    private final Integer MAX_ATTEMPTS = 3;

    @Transactional(rollbackFor = Exception.class)
    public void handleEventPublished(OutboxEvent outboxEvent) {
        outboxEvent.setStatus(OutboxStatus.PUBLISHED);
        outboxEvent.setPublishedAt(LocalDateTime.now());
        outboxEventRepository.save(outboxEvent);
    }

    @Transactional
    public void handleEventFailed(OutboxEvent outboxEvent, String errorMessage) {
        outboxEvent.setAttempts(outboxEvent.getAttempts() + 1);
        outboxEvent.setLastError(
                errorMessage.length() < 1000 ? errorMessage : errorMessage.substring(0, 1000));
        if (outboxEvent.getAttempts() >= MAX_ATTEMPTS) {
            outboxEvent.setStatus(OutboxStatus.FAILED);
        }
        outboxEventRepository.save(outboxEvent);
    }


}
