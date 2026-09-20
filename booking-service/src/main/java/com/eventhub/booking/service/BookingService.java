package com.eventhub.booking.service;

import com.eventhub.booking.dtos.request.CreateBookingRequest;
import com.eventhub.booking.dtos.response.BookingResponse;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request, UUID userId);

    BookingResponse getBookingById(UUID bookingId, UUID currentUserId, boolean admin);

    List<BookingResponse> getMyBookings(UUID currentUserId);

    List<BookingResponse> getBookingsByEventId(UUID eventId, UUID currentUserId, boolean admin);
    BookingResponse cancelBooking(UUID bookingId, UUID currentUserId,boolean admin);
}
