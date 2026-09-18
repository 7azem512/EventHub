package com.eventhub.media.service.impl;

import com.eventhub.media.dto.response.MediaResponse;
import com.eventhub.media.dto.response.MediaUrlResponse;
import com.eventhub.media.entity.MediaFile;
import com.eventhub.media.enums.StorageProvider;
import com.eventhub.media.exception.InvalidMediaFileException;
import com.eventhub.media.exception.MediaAccessDeniedException;
import com.eventhub.media.exception.MediaNotFoundException;
import com.eventhub.media.mapper.MediaMapper;
import com.eventhub.media.repository.MediaFileRepository;
import com.eventhub.media.service.MediaService;
import com.eventhub.media.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MediaServiceImpl implements MediaService {
    private final MediaFileRepository mediaFileRepository;
    private final MediaMapper mediaMapper;
    private final StorageService storageService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;


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

    @Override
    public MediaResponse uploadMedia(MultipartFile file, UUID userId) {
        validateFile(file);

        String storageKey = storageService.upload(file, userId);

        try {

            MediaFile mediaFile = MediaFile.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storageKey(storageKey)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .storageProvider(StorageProvider.MINIO)
                    .uploadedBy(userId)
                    .build();

            MediaFile savedMedia =
                    mediaFileRepository.save(mediaFile);

            return mediaMapper.toResponse(savedMedia);

        } catch (RuntimeException ex) {

            try {
                storageService.delete(storageKey);
            } catch (RuntimeException cleanupException) {
                ex.addSuppressed(cleanupException);
            }

            throw ex;
        }
    }


    @Override
    public MediaUrlResponse getMediaUrl(UUID id) {

        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Media file not found with id: " +id));

        String url = storageService.generateAccessUrl(
                mediaFile.getStorageKey()
        );

        return new MediaUrlResponse(
                mediaFile.getId(),
                url
        );
    }

    @Override
    @Transactional
    public void deleteMedia(
            UUID id,
            UUID currentUserId,
            boolean admin
    ) {

        MediaFile mediaFile = mediaFileRepository.findById(id)
                .orElseThrow(() -> new MediaNotFoundException("Media file not found with id: " + id));

        if (!admin &&
                !mediaFile.getUploadedBy().equals(currentUserId)) {

            throw new MediaAccessDeniedException();
        }

        mediaFileRepository.delete(mediaFile);
        mediaFileRepository.flush();

        storageService.delete(mediaFile.getStorageKey());
    }


    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidMediaFileException(
                    "File cannot be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidMediaFileException(
                    "File size cannot exceed 10 MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(
                        contentType.equals("image/jpeg")
                                || contentType.equals("image/png")
                                || contentType.equals("image/webp")
                )) {

            throw new InvalidMediaFileException(
                    "Only JPEG, PNG and WEBP images are allowed"
            );
        }
    }
}
