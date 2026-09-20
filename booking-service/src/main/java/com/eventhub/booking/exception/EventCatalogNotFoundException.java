package com.eventhub.booking.exception;

public class EventCatalogNotFoundException extends RuntimeException {
    public EventCatalogNotFoundException(String message) {
        super(message);
    }
}
