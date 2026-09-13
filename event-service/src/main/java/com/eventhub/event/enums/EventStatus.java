package com.eventhub.event.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Current lifecycle status of the event")
public enum EventStatus {
    DRAFT,
    PENDING_APPROVAL,
    PUBLISHED,
    REJECTED,
    CANCELLED,
    COMPLETED
}
