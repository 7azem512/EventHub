package com.eventhub.booking.reservation;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingReservation {
    private UUID bookingId;
    private UUID eventId;
    private UUID ticketTypeId;
    private UUID userId;
    private Integer quantity;
    private LocalDateTime createdAt;
}
