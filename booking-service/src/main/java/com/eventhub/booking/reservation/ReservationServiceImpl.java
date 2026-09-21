package com.eventhub.booking.reservation;

import com.eventhub.booking.config.BookingReservationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final RedisTemplate<String, BookingReservation> redisTemplate;
    private final BookingReservationProperties properties;

    @Override
    public void createReservation(BookingReservation reservation) {

        String key =
                ReservationKeys.bookingReservation(reservation.getBookingId());

        redisTemplate.opsForValue().set(
                key,
                reservation,
                properties.getTtl()
        );
    }

    @Override
    public BookingReservation getReservation(UUID bookingId) {
        String key = ReservationKeys.bookingReservation(bookingId);

        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean exists(UUID bookingId) {
        String key = ReservationKeys.bookingReservation(bookingId);

        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void deleteReservation(UUID bookingId) {
        String key = ReservationKeys.bookingReservation(bookingId);

        redisTemplate.delete(key);
    }
}