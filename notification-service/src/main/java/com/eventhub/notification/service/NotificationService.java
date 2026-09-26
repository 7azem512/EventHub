package com.eventhub.notification.service;

import com.eventhub.notification.dto.response.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationResponse> getMyNotifications(UUID currentUserId);

    NotificationResponse markAsRead(UUID notificationId, UUID currentUserId);
}