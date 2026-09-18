package com.eventhub.media.exception;

public class MediaNotFoundException extends RuntimeException{
    public MediaNotFoundException(String message) {
        super(message);
    }
}
