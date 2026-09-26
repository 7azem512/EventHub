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
import com.eventhub.booking.exception.InsufficientCapacityException;
import com.eventhub.booking.exception.InvalidBookingStateException;
import com.eventhub.booking.mapper.BookingMapper;
import com.eventhub.booking.messaging.event.BookingEvent;
import com.eventhub.booking.messaging.event.BookingEventPayload;
import com.eventhub.booking.messaging.event.BookingEventType;
import com.eventhub.booking.messaging.outbox.OutboxEventWriter;
import com.eventhub.booking.repository.BookingRepository;
import com.eventhub.booking.reservation.BookingReservation;
import com.eventhub.booking.reservation.ReservationService;
import com.eventhub.booking.reservation.ReservedTicketCounterService;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.eventhub.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EventCatalogClient eventCatalogClient;
    private final ReservationService reservationService;
    private final ReservedTicketCounterService reservedTicketCounterService;
    private final OutboxEventWriter outboxEventWriter;




    @Override
    public BookingResponse createBooking(CreateBookingRequest request, UUID currentUserId) {

        EventInfo eventInfo = eventCatalogClient.getEventInfo(request.getEventId());

        if (!"PUBLISHED".equals(eventInfo.getStatus())) {
            throw new InvalidBookingStateException("Event is not published and cannot accept bookings");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(eventInfo.getBookingStartDate())) {
            throw new InvalidBookingStateException("Booking has not started yet for this event");
        }

        if (now.isAfter(eventInfo.getBookingEndDate())) {
            throw new InvalidBookingStateException("Booking has already ended for this event");
        }

        TicketTypeInfo ticketTypeInfo = eventCatalogClient.getTicketTypeInfo(request.getEventId(), request.getTicketTypeId());

        boolean reserved = reservedTicketCounterService.reserveTickets(request.getTicketTypeId(), request.getQuantity(), ticketTypeInfo.getCapacity());

        if (!reserved) {
            throw new InsufficientCapacityException("Not enough tickets available for ticket type: " + request.getTicketTypeId());
        }

        UUID ticketTypeId = request.getTicketTypeId();
        int quantity = request.getQuantity();

        TransactionSynchronizationManager.registerSynchronization(

                new TransactionSynchronization() {

                    @Override
                    public void afterCompletion(int status) {
                        if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                            reservedTicketCounterService.releaseTickets(ticketTypeId, quantity);
                        }
                    }
                }
        );

        BigDecimal ticketPrice = ticketTypeInfo.getPrice();

        BigDecimal totalAmount =
                ticketPrice.multiply(
                        BigDecimal.valueOf(request.getQuantity())
                );

        Booking booking = Booking.builder()
                .eventId(ticketTypeInfo.getEventId())
                .unitPrice(ticketPrice)
                .totalAmount(totalAmount)
                .ticketTypeId(request.getTicketTypeId())
                .userId(currentUserId)
                .quantity(request.getQuantity())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        BookingReservation reservation = BookingReservation.builder()
                .bookingId(savedBooking.getId())
                .userId(savedBooking.getUserId())
                .eventId(savedBooking.getEventId())
                .ticketTypeId(savedBooking.getTicketTypeId())
                .quantity(savedBooking.getQuantity())
                .createdAt(savedBooking.getCreatedAt())
                .build();

        reservationService.createReservation(reservation);

        return bookingMapper.toResponse(savedBooking);
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
    public BookingResponse cancelBooking(UUID bookingId,  UUID currentUserId, boolean admin) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found with id: " + bookingId
                        )
                );

        if (!admin && !booking.getUserId().equals(currentUserId)) {
            throw new BookingAccessDeniedException(
                    "You are not authorized to cancel this booking"
            );
        }

        int updatedRows = bookingRepository.updateStatusIfCurrent(
                bookingId,
                BookingStatus.PENDING,
                BookingStatus.CANCELLED
        );

        if (updatedRows == 0) {
            throw new InvalidBookingStateException(
                    "Booking is not in PENDING state"
            );
        }

        UUID currentBookingId = booking.getId();
        UUID ticketTypeId = booking.getTicketTypeId();
        int quantity = booking.getQuantity();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        reservationService.deleteReservation(currentBookingId);

                        reservedTicketCounterService.releaseTickets(
                                ticketTypeId,
                                quantity
                        );
                    }
                }
        );

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingMapper.toResponse(booking);
    }


    @Override
    public void expireBooking(UUID bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null) {
            return;
        }

        int updatedRows = bookingRepository.updateStatusIfCurrent(bookingId, BookingStatus.PENDING, BookingStatus.EXPIRED);

        if (updatedRows == 0) {
            return;
        }

        UUID ticketTypeId = booking.getTicketTypeId();
        int quantity = booking.getQuantity();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        reservedTicketCounterService.releaseTickets(ticketTypeId, quantity);
                    }
                }
        );
    }

    @Transactional
    @Override
    public BookingResponse confirmBooking(
            UUID bookingId,
            UUID currentUserId,
            boolean admin
    ) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(
                                "Booking not found with id: " + bookingId
                        )
                );

        if (!admin && !booking.getUserId().equals(currentUserId)) {
            throw new BookingAccessDeniedException(
                    "You are not authorized to confirm this booking"
            );
        }

        int updatedRows = bookingRepository.updateStatusIfCurrent(
                bookingId,
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED
        );

        if (updatedRows == 0) {
            throw new InvalidBookingStateException(
                    "Booking is not in PENDING state"
            );
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        BookingEvent event = new BookingEvent(
                UUID.randomUUID(),
                BookingEventType.BOOKING_CONFIRMED,
                booking.getId(),
                Instant.now(),
                new BookingEventPayload(
                        booking.getUserId(),
                        booking.getEventId(),
                        booking.getTicketTypeId(),
                        booking.getQuantity(),
                        booking.getTotalAmount()
                )
        );

        outboxEventWriter.save(event);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        reservationService.deleteReservation(bookingId);
                    }
                }
        );

        return bookingMapper.toResponse(booking);
    }
}
