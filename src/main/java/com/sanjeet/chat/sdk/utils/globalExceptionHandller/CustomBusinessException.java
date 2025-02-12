package com.sanjeet.chat.sdk.utils.globalExceptionHandller;


import org.springframework.http.HttpStatus;


public class CustomBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;

    public CustomBusinessException(ErrorCode errorCode, HttpStatus status) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode.getCode();
    }

    public String getErrorMessage() {
        return errorCode.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return status;
    }
}


