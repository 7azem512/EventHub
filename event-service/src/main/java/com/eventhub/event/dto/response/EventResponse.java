package com.eventhub.event.dto.response;

import com.eventhub.event.enums.EventStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventResponse {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private UUID organizerId;
    private CategoryResponse category;

    private EventStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
