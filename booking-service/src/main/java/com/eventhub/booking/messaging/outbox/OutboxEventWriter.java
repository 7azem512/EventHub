package com.eventhub.booking.messaging.outbox;

import com.eventhub.booking.messaging.event.BookingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OutboxEventWriter {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void save(BookingEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .id(event.messageId())
                    .aggregateType("BOOKING")
                    .aggregateId(event.aggregateId())
                    .eventType(event.eventType().name())
                    .payload(payload)
                    .occurredAt(event.occurredAt())
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to serialize booking event for outbox",
                    e
            );
        }
    }
}