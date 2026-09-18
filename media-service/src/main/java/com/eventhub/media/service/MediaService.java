package com.eventhub.media.service;

import com.eventhub.media.dto.response.MediaResponse;
import com.eventhub.media.dto.response.MediaUrlResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MediaService {
    MediaResponse getMediaById(UUID id);
    List<MediaResponse> getMediaByUserId(UUID userId);
    MediaResponse uploadMedia(MultipartFile file, UUID userId);
    MediaUrlResponse getMediaUrl(UUID id);
    void deleteMedia(UUID id, UUID currentUserId, boolean admin);
}
