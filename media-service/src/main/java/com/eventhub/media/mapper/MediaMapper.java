package com.eventhub.media.mapper;

import com.eventhub.media.dto.response.MediaResponse;
import com.eventhub.media.entity.MediaFile;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper {
    public MediaResponse toResponse (MediaFile mediaFile){
        return new MediaResponse(mediaFile.getId(),
                mediaFile.getOriginalFileName(),
                mediaFile.getContentType(),
                mediaFile.getSize(),
                mediaFile.getStorageProvider(),
                mediaFile.getUploadedBy(),
                mediaFile.getCreatedAt());
    }

}
