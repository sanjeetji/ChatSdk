package com.sanjeet.chat.sdk.utils.globalExceptionHandller;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
