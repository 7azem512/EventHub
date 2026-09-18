package com.eventhub.media.storage.impl;

import com.eventhub.media.exception.MediaNotFoundException;
import com.eventhub.media.exception.MediaStorageException;
import com.eventhub.media.storage.StorageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.provider", havingValue = "minio", matchIfMissing = true)
public class MinioStorageService implements StorageService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;


    @Override
    public String upload(MultipartFile file, UUID userId) {
       String storageKey = "users/"+userId+"/"+UUID.randomUUID();
       try {
           minioClient.putObject(
                   PutObjectArgs.builder()
                           .bucket(bucketName)
                           .object(storageKey)
                           .stream(
                                   file.getInputStream(),
                                   file.getSize(),
                                   -1
                           )
                           .contentType(file.getContentType())
                           .build()
           );

           return storageKey;
       }catch (Exception e) {
           throw new MediaStorageException("Error occurred while uploading file", e);
       }
    }

    @Override
    public void delete(String fileName) {
        try {
            minioClient.removeObject(
                   RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        }catch (Exception ex) {
            throw new MediaStorageException("Error occurred while deleting file", ex);
        }
    }

    @Override
    public String generateAccessUrl(String storageKey) {
        try {

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(storageKey)
                            .expiry(15, TimeUnit.MINUTES)
                            .build()
            );

        } catch (Exception ex) {

            throw new MediaStorageException(
                    "Failed to generate media URL",
                    ex
            );}
    }
}
