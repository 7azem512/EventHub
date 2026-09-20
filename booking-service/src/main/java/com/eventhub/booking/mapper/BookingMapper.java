package com.eventhub.booking.mapper;

import com.eventhub.booking.dtos.response.BookingResponse;
import com.eventhub.booking.entity.Booking;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingResponse toResponse(Booking booking){
    return BookingResponse.builder()
            .id(booking.getId())
            .userId(booking.getUserId())
            .eventId(booking.getEventId())
            .ticketTypeId(booking.getTicketTypeId())
            .quantity(booking.getQuantity())
            .unitPrice(booking.getUnitPrice())
            .totalAmount(booking.getTotalAmount())
            .status(booking.getStatus())
            .createdAt(booking.getCreatedAt())
            .updatedAt(booking.getUpdatedAt())
            .build();
    }
}