package com.eventhub.booking.service.impl;

import com.eventhub.booking.client.EventCatalogClient;
import com.eventhub.booking.client.dto.EventInfo;
import com.eventhub.booking.client.dto.TicketTypeInfo;
import com.eventhub.booking.dtos.request.CreateBookingRequest;
import com.eventhub.booking.dtos.response.BookingResponse;
import com.eventhub.booking.entity.Booking;
import com.eventhub.booking.enums.BookingStatus;
import com.eventhub.booking.exception.BookingAccessDeniedException;
import com.eventhub.booking.exception.BookingNotFoundException;
import com.eventhub.booking.exception.InvalidBookingStateException;
import com.eventhub.booking.mapper.BookingMapper;
import com.eventhub.booking.repository.BookingRepository;
import com.eventhub.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EventCatalogClient eventCatalogClient;
    @Override
    public BookingResponse createBooking(CreateBookingRequest request, UUID currentUserId) {
        TicketTypeInfo ticketTypeInfo = eventCatalogClient.getTicketTypeInfo(request.getEventId(), request.getTicketTypeId());

        BigDecimal ticketPrice = ticketTypeInfo.getPrice();
        BigDecimal totalAmount = ticketPrice.multiply(BigDecimal.valueOf(request.getQuantity()));
        Booking booking=Booking.builder()
                .eventId(ticketTypeInfo.getEventId())
                .unitPrice(ticketPrice)
                .totalAmount(totalAmount)
                .ticketTypeId(request.getTicketTypeId())
                .userId(currentUserId)
                .quantity(request.getQuantity())
                .status(BookingStatus.PENDING)
                .build();

        return bookingMapper.toResponse(bookingRepository.save(booking));

    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(UUID bookingId, UUID currentUserId, boolean admin) {
        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: "+bookingId));

        if (!admin && !booking.getUserId().equals(currentUserId)) {
            throw new BookingAccessDeniedException("You are not authorized to view this booking");
        }
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(UUID currentUserId) {
        List<Booking> bookings = bookingRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId);

        return bookings.stream().map(bookingMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByEventId(UUID eventId, UUID currentUserId, boolean admin) {
        EventInfo event = eventCatalogClient.getEventInfo(eventId);

        if (!admin && !event.getOrganizerId().equals(currentUserId)) {
            throw new BookingAccessDeniedException("You are not authorized to view bookings for this event");
        }

        List<Booking> bookings = bookingRepository.findAllByEventIdOrderByCreatedAtDesc(eventId);

        return bookings.stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    public BookingResponse cancelBooking(UUID bookingId, UUID currentUserId, boolean admin)
    {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: "+bookingId));

        if (!admin && !booking.getUserId().equals(currentUserId)) {
            throw new BookingAccessDeniedException("You are not authorized to cancel this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingStateException("Booking is not in PENDING state");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingMapper.toResponse(booking);
    }
}
