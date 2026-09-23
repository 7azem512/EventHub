CREATE TABLE media_files (
                             id UUID NOT NULL,
                             content_type VARCHAR(255) NOT NULL,
                             created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                             original_file_name VARCHAR(255) NOT NULL,
                             size BIGINT NOT NULL,
                             storage_key VARCHAR(255) NOT NULL,
                             storage_provider VARCHAR(255) NOT NULL,
                             uploaded_by UUID NOT NULL,

                             CONSTRAINT pk_media_files PRIMARY KEY (id),

                             CONSTRAINT uk_media_files_storage_key
                                 UNIQUE (storage_key),

                             CONSTRAINT chk_media_files_storage_provider
                                 CHECK (storage_provider IN ('MINIO', 'CLOUDINARY'))
);