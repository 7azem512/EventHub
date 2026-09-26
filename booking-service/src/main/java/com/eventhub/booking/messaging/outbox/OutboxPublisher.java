package com.eventhub.booking.messaging.outbox;

import com.eventhub.booking.messaging.event.BookingEvent;
import com.eventhub.booking.messaging.publisher.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final BookingEventPublisher bookingEventPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "${app.kafka.outbox.publish-delay-ms:1000}")
    @Transactional
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop100ByPublishedAtIsNullOrderByCreatedAtAsc();

        for (OutboxEvent outboxEvent : events) {

            try {
                BookingEvent bookingEvent =
                        objectMapper.readValue(
                                outboxEvent.getPayload(),
                                BookingEvent.class
                        );

                bookingEventPublisher
                        .publish(bookingEvent)
                        .join();

                outboxEvent.setPublishedAt(Instant.now());

                log.info(
                        "Outbox event published. messageId={}, type={}, aggregateId={}",
                        outboxEvent.getId(),
                        outboxEvent.getEventType(),
                        outboxEvent.getAggregateId()
                );

            } catch (Exception e) {
                log.error(
                        "Failed to publish outbox event. messageId={}",
                        outboxEvent.getId(),
                        e
                );

                break;
            }
        }
    }
}