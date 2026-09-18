package com.eventhub.media.exception;

public class MediaAccessDeniedException extends RuntimeException{
    public MediaAccessDeniedException() {
        super("You are not allowed to modify this media file");
    }
}
