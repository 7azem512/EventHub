package com.eventhub.notification.dto.response;

import com.eventhub.notification.persistence.notification.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        NotificationType type,
        String title,
        String body,
        String referenceType,
        UUID referenceId,
        Instant readAt,
        Instant createdAt
) {
}