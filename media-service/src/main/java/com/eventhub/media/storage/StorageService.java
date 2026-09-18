package com.eventhub.media.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {
    String upload(MultipartFile file, UUID userId);
    void delete(String storageKey);
    String generateAccessUrl(String storageKey);
}
