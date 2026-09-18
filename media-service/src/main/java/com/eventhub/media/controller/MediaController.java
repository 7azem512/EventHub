package com.eventhub.media.controller;

import com.eventhub.media.dto.response.MediaResponse;
import com.eventhub.media.dto.response.MediaUrlResponse;
import com.eventhub.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> uploadMedia(
            @RequestPart("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt)
    {
        UUID userId = UUID.fromString(jwt.getSubject());
        MediaResponse response = mediaService.uploadMedia(file, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}/url")
    public ResponseEntity<MediaUrlResponse> getMediaUrl(@PathVariable UUID id)
    {
        return ResponseEntity.ok(mediaService.getMediaUrl(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt)
    {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        boolean admin = isAdmin(jwt);
        mediaService.deleteMedia(id, currentUserId, admin);

        return ResponseEntity.noContent().build();
    }








    private boolean isAdmin(Jwt jwt) {

        Map<String, Object> realmAccess =
                jwt.getClaimAsMap("realm_access");

        if (realmAccess == null) {
            return false;
        }

        Object rolesObject =
                realmAccess.get("roles");

        if (!(rolesObject instanceof Collection<?> roles)) {
            return false;
        }

        return roles.stream()
                .map(Object::toString)
                .anyMatch("ADMIN"::equals);
    }
}
