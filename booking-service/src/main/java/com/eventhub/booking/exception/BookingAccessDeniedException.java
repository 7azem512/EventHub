package com.eventhub.booking.exception;

public class BookingAccessDeniedException extends RuntimeException{
    public BookingAccessDeniedException(String message) {
        super(message);
    }
}
