package com.eventhub.notification.controller;

import com.eventhub.notification.dto.response.NotificationResponse;
import com.eventhub.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
                notificationService.getMyNotifications(currentUserId)
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId,
                        currentUserId
                )
        );
    }
}