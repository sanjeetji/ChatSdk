package com.sanjeet.chat.sdk.utils.globalExceptionHandller;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
