package com.eventhub.booking.reservation;

import com.eventhub.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpirationListener implements MessageListener {

    private static final String RESERVATION_PREFIX = "booking:reservation:";

    private final BookingService bookingService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String expiredKey =
                new String(message.getBody(), StandardCharsets.UTF_8);

        if (!expiredKey.startsWith(RESERVATION_PREFIX)) {
            return;
        }

        UUID bookingId =
                ReservationKeys.extractBookingId(expiredKey);

        bookingService.expireBooking(bookingId);

        log.info(
                "Booking reservation expired for bookingId={}",
                bookingId
        );
    }
}