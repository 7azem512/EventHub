package com.eventhub.booking.messaging.publisher;

import com.eventhub.booking.messaging.event.BookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBookingEventPublisher implements BookingEventPublisher {

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Value("${app.kafka.topics.booking-events}")
    private String bookingEventsTopic;

    @Override
    public CompletableFuture<Void> publish(BookingEvent event) {

        String key = event.aggregateId().toString();

        return kafkaTemplate
                .send(bookingEventsTopic, key, event)
                .thenAccept(result ->
                        log.info(
                                "Published booking event. type={}, bookingId={}, topic={}, partition={}, offset={}",
                                event.eventType(),
                                event.aggregateId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        )
                );
    }
}