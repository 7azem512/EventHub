package com.eventhub.booking.messaging.publisher;

import com.eventhub.booking.messaging.event.BookingEvent;

import java.util.concurrent.CompletableFuture;

public interface BookingEventPublisher {

    CompletableFuture<Void> publish(BookingEvent event);
}