package com.eventhub.booking.messaging.event;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingEventPayload(
        UUID userId,
        UUID eventId,
        UUID ticketTypeId,
        Integer quantity,
        BigDecimal totalAmount
) {

}