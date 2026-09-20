package com.eventhub.booking.client;

import com.eventhub.booking.client.dto.EventInfo;
import com.eventhub.booking.client.dto.TicketTypeInfo;

import java.util.UUID;

public interface EventCatalogClient {
    TicketTypeInfo getTicketTypeInfo( UUID eventId, UUID ticketTypeId);
    EventInfo getEventInfo(UUID eventId);
}
