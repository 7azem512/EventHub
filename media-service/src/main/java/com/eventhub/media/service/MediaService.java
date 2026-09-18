package com.eventhub.media.service;

import com.eventhub.media.dto.response.MediaResponse;

import java.util.List;
import java.util.UUID;

public interface MediaService {
    MediaResponse getMediaById(UUID id);
    List<MediaResponse> getMediaByUserId(UUID userId);
}
