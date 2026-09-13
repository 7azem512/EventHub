package com.eventhub.event.services;

import com.eventhub.event.dto.request.CreateTicketTypeRequest;
import com.eventhub.event.dto.request.UpdateTicketTypeRequest;
import com.eventhub.event.dto.response.TicketTypeResponse;

import java.util.List;
import java.util.UUID;

public interface TicketTypeService {
    TicketTypeResponse createTicketType(
            UUID eventId,
            CreateTicketTypeRequest request
    );

    TicketTypeResponse updateTicketType(
            UUID eventId,
            UUID ticketTypeId,
            UpdateTicketTypeRequest request
    );

    TicketTypeResponse getTicketTypeById(
            UUID eventId,
            UUID ticketTypeId
    );

    List<TicketTypeResponse> getTicketTypesByEvent(
            UUID eventId
    );

    void deleteTicketType(
            UUID eventId,
            UUID ticketTypeId
    );
}
