package com.sanjeet.chat.sdk.utils;

import java.util.Arrays;

public class Constant {

    public static final String H_MAC_ALGORITHM = "HmacSHA256";

    public static final String [] PUBLIC_URLS =
                                                {
                                                    "/api/client/register",
                                                    "/api/client/login",
                                                     "/api/admin/greeting",
                                                     "/api/admin/register",
                                                      "/api/admin/login",
                                                        "/api/user/register",
                                                };
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String CLAIM_SESSION_TOKEN = "claim_session_token";
    public static final String USER = "USER";
    public static final String CLIENT = "CLIENT";
    public static final String ADMIN = "ADMIN";
    public static final String ROLE = "role";
    public static final String USER_NAME = "user_name";
    public static final String CLIENT_ID = "clientId";
    public static final String API_KEY = "apiKey";
    public static final String FAILED_TO_REGISTER = " Failed to register";
    public static final String FAILED_TO_LOGIN = " Failed to login";
    public static final String EMPTY_STRING = "";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String YOU_ARE_NOT_AUTHORIZED = "You are not authorized";
    public static final String LOGIN_SUCCESS = "Login Successful.";
    public static final String REGISTRATION_SUCCESS = "Congratulation, You have register successful.";
    public static final String REGISTRATION_FAILED = "Sorry, Registration has been failed.";

    static {
        System.out.println("PUBLIC_URLS: " + Arrays.toString(PUBLIC_URLS)); // Log the URLs
    }

}
