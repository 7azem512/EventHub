package com.eventhub.booking.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {
    @NotNull(message = "Event ID cannot be null")
    private UUID eventId;
    @NotNull(message = "Ticket type ID cannot be null")
    private UUID ticketTypeId;
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
