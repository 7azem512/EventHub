package com.eventhub.booking.reservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Test
    void shouldCreateReservation() {

        UUID bookingId = UUID.randomUUID();

        BookingReservation reservation = createReservation(bookingId);

        reservationService.createReservation(reservation);

        assertTrue(reservationService.exists(bookingId));

        // cleanup
        reservationService.deleteReservation(bookingId);
    }

    @Test
    void shouldGetReservation() {

        UUID bookingId = UUID.randomUUID();

        BookingReservation reservation = createReservation(bookingId);

        reservationService.createReservation(reservation);

        BookingReservation result =
                reservationService.getReservation(bookingId);

        assertNotNull(result);
        assertEquals(bookingId, result.getBookingId());
        assertEquals(reservation.getUserId(), result.getUserId());
        assertEquals(reservation.getEventId(), result.getEventId());
        assertEquals(reservation.getTicketTypeId(), result.getTicketTypeId());
        assertEquals(2, result.getQuantity());

        // cleanup
        reservationService.deleteReservation(bookingId);
    }

    @Test
    void shouldReturnTrueWhenReservationExists() {

        UUID bookingId = UUID.randomUUID();

        BookingReservation reservation = createReservation(bookingId);

        reservationService.createReservation(reservation);

        boolean exists = reservationService.exists(bookingId);

        assertTrue(exists);

        // cleanup
        reservationService.deleteReservation(bookingId);
    }

    @Test
    void shouldDeleteReservation() {

        UUID bookingId = UUID.randomUUID();

        BookingReservation reservation = createReservation(bookingId);

        reservationService.createReservation(reservation);

        assertTrue(reservationService.exists(bookingId));

        reservationService.deleteReservation(bookingId);

        assertFalse(reservationService.exists(bookingId));
    }

    private BookingReservation createReservation(UUID bookingId) {

        return BookingReservation.builder()
                .bookingId(bookingId)
                .userId(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .ticketTypeId(UUID.randomUUID())
                .quantity(2)
                .createdAt(LocalDateTime.now())
                .build();
    }
}