package com.eventhub.media.dto.response;

import com.eventhub.media.enums.StorageProvider;

import java.time.LocalDateTime;
import java.util.UUID;

public record MediaResponse(
        UUID id,
        String originalFileName,
        String contentType,
        Long size,
        StorageProvider storageProvider,
        UUID uploadedBy,
        LocalDateTime createdAt
) {
}
