package com.eventhub.media.storage.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.eventhub.media.enums.StorageProvider;
import com.eventhub.media.exception.MediaStorageException;
import com.eventhub.media.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "cloudinary.enabled",
        havingValue = "true"
)
@Slf4j
public class CloudinaryStorageService implements StorageService {

    private final Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file, UUID userId) {

        String publicId =
                "eventhub/users/"
                        + userId
                        + "/"
                        + UUID.randomUUID();

        try {

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "image",
                            "type", "authenticated"
                    )
            );

            return uploadResult.get("public_id").toString();

        } catch (Exception ex) {
            log.error("Failed to upload file to Cloudinary", ex);
            throw new MediaStorageException(
                    "Failed to upload file to Cloudinary",
                    ex
            );
        }
    }

    @Override
    public void delete(String storageKey) {

        try {

            cloudinary.uploader().destroy(
                    storageKey,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "type", "authenticated",
                            "invalidate", true
                    )
            );

        } catch (Exception ex) {
            log.error("Failed to delete file from Cloudinary", ex);

            throw new MediaStorageException(
                    "Failed to delete file from Cloudinary",
                    ex
            );
        }
    }

    @Override
    public String generateAccessUrl(String storageKey) {

        try {

            return cloudinary.url()
                    .resourceType("image")
                    .type("authenticated")
                    .secure(true)
                    .signed(true)
                    .generate(storageKey);

        } catch (Exception ex) {

            throw new MediaStorageException(
                    "Failed to generate Cloudinary URL",
                    ex
            );
        }
    }

    @Override
    public StorageProvider getProvider() {
        return StorageProvider.CLOUDINARY;
    }
}