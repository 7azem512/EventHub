package com.eventhub.media.dto.response;

import java.util.UUID;

public record MediaUrlResponse(
        UUID mediaId,
        String url
) {
}
