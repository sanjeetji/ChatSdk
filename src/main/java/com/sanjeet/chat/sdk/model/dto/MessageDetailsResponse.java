package com.sanjeet.chat.sdk.model.dto;

public class MessageDetailsResponse {

    private long id;
    private String message;

    public MessageDetailsResponse(long id, String message) {
        this.id = id;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageDetailsRequest{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
