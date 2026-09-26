package com.razorpay.payment.outbox;

import com.razorpay.common.enums.EventAggregateType;
import com.razorpay.payment.entity.OutboxEvent;
import com.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;

    public void publish(EventAggregateType aggregateType, UUID aggregateId, String eventType,
                        Map<String, Object> payload) {
        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .build();
        log.info("Saving outbox Pyment event with EventType: {} and PaymentId: {}", eventType, aggregateId);
        outboxEventRepository.save(outboxEvent);
    }
}
