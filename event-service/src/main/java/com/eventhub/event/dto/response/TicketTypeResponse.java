package com.eventhub.event.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketTypeResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private Integer capacity;
    private UUID eventId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
