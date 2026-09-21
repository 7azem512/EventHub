package com.eventhub.booking.reservation;

import java.util.UUID;

public final class ReservationKeys {

    private static final String PREFIX = "booking:reservation:";
    private static final String RESERVED="booking:reserved:";

    private ReservationKeys() {
    }

    public static String bookingReservation(UUID bookingId) {
        return PREFIX + bookingId;
    }

    public static String bookingReserved(UUID ticketTypeId) {
        return RESERVED+ticketTypeId;
    }

    public static UUID extractBookingId(String key) {

        if (key == null || !key.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Invalid booking reservation key");
        }

        String bookingId = key.substring(PREFIX.length());

        return UUID.fromString(bookingId);
    }
}