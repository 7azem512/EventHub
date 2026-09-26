package com.eventhub.notification.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record BookingEvent(
        UUID messageId,
        BookingEventType eventType,
        UUID aggregateId,
        Instant occurredAt,
        BookingEventPayload payload
) {
}
