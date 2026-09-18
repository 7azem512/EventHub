package com.eventhub.media.service.impl;

import com.eventhub.media.dto.response.MediaResponse;
import com.eventhub.media.entity.MediaFile;
import com.eventhub.media.exception.MediaNotFoundException;
import com.eventhub.media.mapper.MediaMapper;
import com.eventhub.media.repository.MediaFileRepository;
import com.eventhub.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaServiceImpl implements MediaService {
    private final MediaFileRepository mediaFileRepository;
    private final MediaMapper mediaMapper;


    @Override
    @Transactional(readOnly = true)
    public MediaResponse getMediaById(UUID id) {
        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Media not found with id: " + id));
        return mediaMapper.toResponse(mediaFile);
    }

    @Override
    public List<MediaResponse> getMediaByUserId(UUID userId) {
        return mediaFileRepository.findByUploadedBy(userId)
                .stream()
                .map(mediaMapper::toResponse)
                .toList();
    }
}
