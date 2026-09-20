package com.eventhub.booking.client.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventInfo {

    private UUID id;
    private UUID organizerId;
}