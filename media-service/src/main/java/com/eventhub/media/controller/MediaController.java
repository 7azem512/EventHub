package com.eventhub.media.controller;

import com.eventhub.media.dto.response.MediaResponse;
import com.eventhub.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;


    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getMediaById(@PathVariable UUID id) {
        MediaResponse mediaResponse = mediaService.getMediaById(id);
        return ResponseEntity.ok(mediaResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<List<MediaResponse>> getMediaByUserId(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<MediaResponse> mediaResponses = mediaService.getMediaByUserId(userId);
        return ResponseEntity.ok(mediaResponses);
    }
}
