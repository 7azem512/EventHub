package com.eventhub.media.entity;

import com.eventhub.media.enums.StorageProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "media_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true)
    private String storageKey;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageProvider storageProvider;

    @Column(nullable = false)
    private UUID uploadedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}