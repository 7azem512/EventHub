package com.eventhub.notification.service.impl;

import com.eventhub.notification.dto.response.NotificationResponse;
import com.eventhub.notification.exception.NotificationNotFoundException;
import com.eventhub.notification.persistence.notification.Notification;
import com.eventhub.notification.persistence.notification.NotificationRepository;
import com.eventhub.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(
            UUID currentUserId
    ) {

        return notificationRepository
                .findAllByUserIdOrderByCreatedAtDesc(currentUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(
            UUID notificationId,
            UUID currentUserId
    ) {

        Notification notification =
                notificationRepository
                        .findByIdAndUserId(
                                notificationId,
                                currentUserId
                        )
                        .orElseThrow(() ->
                                new NotificationNotFoundException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        notification.markAsRead();

        return toResponse(notification);
    }

    private NotificationResponse toResponse(
            Notification notification
    ) {

        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getReferenceType(),
                notification.getReferenceId(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }
}