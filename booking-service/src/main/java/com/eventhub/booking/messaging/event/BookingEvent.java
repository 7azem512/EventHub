package com.eventhub.booking.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingEvent(
        UUID messageId,
        BookingEventType eventType,
        UUID aggregateId,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant occurredAt,
        BookingEventPayload payload
) {
}
