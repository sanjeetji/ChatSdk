package com.sanjeet.chat.sdk.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsResponse {

    private long userId;
    private String username;
    private String userImage;
    private String phoneNumber;
    private String apiKey;
    private String userAccessToken;
    private Date createdAt;


    public UserDetailsResponse() {
    }

    public UserDetailsResponse(long userId, String username, String userImage, String phoneNumber, String apiKey, String userAccessToken, Date createdAt) {
        this.userId = userId;
        this.username = username;
        this.userImage = userImage;
        this.phoneNumber = phoneNumber;
        this.apiKey = apiKey;
        this.userAccessToken = userAccessToken;
        this.createdAt = createdAt;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUserAccessToken() {
        return userAccessToken;
    }

    public void setUserAccessToken(String userAccessToken) {
        this.userAccessToken = userAccessToken;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public UserDetailsResponse(long userId) {
        this.userId = userId;
    }

}
