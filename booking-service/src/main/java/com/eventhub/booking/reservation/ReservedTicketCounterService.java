package com.eventhub.booking.reservation;

import java.util.UUID;

public interface ReservedTicketCounterService {

    long getReservedQuantity(UUID ticketTypeId);

    boolean reserveTickets(
            UUID ticketTypeId,
            int quantity,
            int capacity
    );

    void releaseTickets(
            UUID ticketTypeId,
            int quantity
    );
}