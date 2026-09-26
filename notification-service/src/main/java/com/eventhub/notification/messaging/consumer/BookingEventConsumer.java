package com.eventhub.notification.messaging.consumer;

import com.eventhub.notification.messaging.event.BookingEvent;
import com.eventhub.notification.messaging.processor.BookingEventProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class BookingEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(BookingEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final BookingEventProcessor bookingEventProcessor;

    public BookingEventConsumer(
            ObjectMapper objectMapper,
            BookingEventProcessor bookingEventProcessor
    ) {
        this.objectMapper = objectMapper;
        this.bookingEventProcessor = bookingEventProcessor;
    }

    @KafkaListener(topics = "${app.kafka.topics.booking-events}")
    public void consume(ConsumerRecord<String, String> record) {

        try {
            BookingEvent event =
                    objectMapper.readValue(record.value(), BookingEvent.class);

            bookingEventProcessor.process(event);

            log.info(
                    "Booking event consumed successfully. messageId={}, partition={}, offset={}",
                    event.messageId(),
                    record.partition(),
                    record.offset()
            );

        } catch (Exception e) {

            log.error(
                    "Failed to consume booking event. key={}, partition={}, offset={}",
                    record.key(),
                    record.partition(),
                    record.offset(),
                    e
            );

            throw new IllegalStateException(
                    "Failed to consume booking event",
                    e
            );
        }
    }
}