package com.eventhub.notification.messaging.processor;

import com.eventhub.notification.messaging.event.BookingEvent;
import com.eventhub.notification.persistence.notification.Notification;
import com.eventhub.notification.persistence.notification.NotificationRepository;
import com.eventhub.notification.persistence.notification.NotificationType;
import com.eventhub.notification.persistence.processed.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingEventProcessor {

    private final ProcessedEventRepository processedEventRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void process(BookingEvent event) {

        int inserted = processedEventRepository.insertIfAbsent(
                event.messageId(),
                event.eventType().name(),
                event.aggregateId()
        );

        if (inserted == 0) {
            log.info(
                    "Duplicate booking event skipped. messageId={}, type={}, bookingId={}",
                    event.messageId(),
                    event.eventType(),
                    event.aggregateId()
            );
            return;
        }

        switch (event.eventType()) {

            case BOOKING_CREATED ->
                    handleBookingCreated(event);

            case BOOKING_CONFIRMED ->
                    handleBookingConfirmed(event);

            case BOOKING_CANCELLED ->
                    handleBookingCancelled(event);

            case BOOKING_EXPIRED ->
                    handleBookingExpired(event);

            default ->
                    log.info(
                            "Booking event recorded but no notification handler yet. type={}, messageId={}",
                            event.eventType(),
                            event.messageId()
                    );
        }
    }

    private void handleBookingCreated(BookingEvent event) {

        Notification notification = new Notification(
                UUID.randomUUID(),
                event.payload().userId(),
                NotificationType.BOOKING_CREATED,
                "Booking created",
                "Your booking has been created and is waiting for confirmation.",
                "BOOKING",
                event.aggregateId()
        );

        notificationRepository.save(notification);

        log.info(
                "BOOKING_CREATED notification created. notificationId={}, messageId={}, bookingId={}, userId={}",
                notification.getId(),
                event.messageId(),
                event.aggregateId(),
                event.payload().userId()
        );
    }

    private void handleBookingConfirmed(BookingEvent event) {

        Notification notification = new Notification(
                UUID.randomUUID(),
                event.payload().userId(),
                NotificationType.BOOKING_CONFIRMED,
                "Booking confirmed",
                "Your booking has been confirmed successfully.",
                "BOOKING",
                event.aggregateId()
        );

        notificationRepository.save(notification);

        log.info(
                "BOOKING_CONFIRMED notification created. notificationId={}, messageId={}, bookingId={}, userId={}",
                notification.getId(),
                event.messageId(),
                event.aggregateId(),
                event.payload().userId()
        );
    }
    private void handleBookingCancelled(BookingEvent event) {

        Notification notification = new Notification(
                UUID.randomUUID(),
                event.payload().userId(),
                NotificationType.BOOKING_CANCELLED,
                "Booking cancelled",
                "Your booking has been cancelled successfully.",
                "BOOKING",
                event.aggregateId()
        );

        notificationRepository.save(notification);

        log.info(
                "BOOKING_CANCELLED notification created. notificationId={}, messageId={}, bookingId={}, userId={}",
                notification.getId(),
                event.messageId(),
                event.aggregateId(),
                event.payload().userId()
        );
    }
    private void handleBookingExpired(BookingEvent event) {

        Notification notification = new Notification(
                UUID.randomUUID(),
                event.payload().userId(),
                NotificationType.BOOKING_EXPIRED,
                "Booking expired",
                "Your booking reservation has expired.",
                "BOOKING",
                event.aggregateId()
        );

        notificationRepository.save(notification);

        log.info(
                "BOOKING_EXPIRED notification created. notificationId={}, messageId={}, bookingId={}, userId={}",
                notification.getId(),
                event.messageId(),
                event.aggregateId(),
                event.payload().userId()
        );
    }
}