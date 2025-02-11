package com.sanjeet.chat.sdk.utils;

public enum ApiResponseFlag {

    SUCCESS(200),
    UN_AUTHORIZED(401),
    NOT_FOUND(402),
    FAILED(403);


    private final int flag;

    ApiResponseFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public static ApiResponseFlag fromFlag(int flag) {
        return switch (flag) { // Modern `switch` expression
            case 200 -> SUCCESS;
            case 401 -> UN_AUTHORIZED;
            case 402 -> NOT_FOUND;
            case 403 -> FAILED;
            default -> throw new IllegalArgumentException("Invalid Status flag: " + flag);
        };
    }

}
