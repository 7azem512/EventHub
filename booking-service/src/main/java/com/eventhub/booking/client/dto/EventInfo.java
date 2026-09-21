package com.eventhub.booking.client.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventInfo {

    private UUID id;
    private UUID organizerId;
    private String status;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
}