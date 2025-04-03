package com.sanjeet.chat.sdk.utils.globalExceptionHandller;

public enum ErrorCode {

    NOT_AUTHORIZED(400,"You are not authorized to access this resource"),
    FAILED_TO_REGISTER(401,"Failed to register user."),
    USER_IS_ALREADY_REGISTER(402,"User is already register with given credentials."),
    FAILED_TO_LOGIN(403,"Failed to login the user."),
    USER_NOT_FOUND(404, "User is not founded with given credentials."),
    INVALID_API_KEY(405, "Provided API_KEY is invalid."),
    FAILED_TO_EXTRACT_CLAIMS(406,"Failed to extract the claims"),
    CLIENT_IS_NOT_FOUND_FOR_THE_GIVEN_API(407,"Client is not found for the given api or API_KEY is invalid."),
    USER_IS_ALREADY_REGISTERED_WITH_THIS_PHONE(408,"User is already registered with this phone."),
    NO_DATA_FOUNDED(409,"No Data Founded."),
    CLIENT_IS_NOT_ACTIVE(4010,"Client is not found or client is not active."),
    USER_IS_NOT_FOUND(4011,"User is not found with given phone and api key"),
    INTERNAL_ERROR(500, "An unexpected error occurred."),
    RECEIVER_NOT_FOUND(600, "Receiver is not founded."),
    SENDER_RECEIVER_PHONE_NO_CAN_NOT_BE_SAME(601, "Sender and receiver's phone no should be different.");


    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
