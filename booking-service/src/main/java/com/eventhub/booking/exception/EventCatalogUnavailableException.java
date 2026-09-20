package com.eventhub.booking.exception;

public class EventCatalogUnavailableException extends RuntimeException{
    public EventCatalogUnavailableException(String message) {
        super(message);
    }
}
