package com.eventhub.booking.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketTypeInfo {
    private UUID id;
    private UUID eventId;
    private BigDecimal price;
    private Integer capacity;
}
