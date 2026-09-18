package com.eventhub.media.repository;

import com.eventhub.media.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile, UUID> {
    List<MediaFile> findByUploadedBy(UUID uploadedBy);
}
