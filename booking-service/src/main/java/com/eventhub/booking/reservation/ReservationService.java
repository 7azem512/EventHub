package com.eventhub.booking.reservation;

import java.util.UUID;

public interface ReservationService {
    void createReservation(BookingReservation reservation);

    BookingReservation getReservation(UUID bookingId);

    boolean exists(UUID bookingId);

    void deleteReservation(UUID bookingId);
}
