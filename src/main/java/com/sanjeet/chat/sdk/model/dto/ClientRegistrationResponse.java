package com.sanjeet.chat.sdk.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientRegistrationResponse {

    private long id;
    private String name;
    private String companyName;
    private String email;
    private String password;
    private String phoneNo;
    private String apiKey;
    private String secretKey;
    private boolean isActive;
    private String accessToken;
    private Date createdAt;

    public ClientRegistrationResponse() {
    }

    public ClientRegistrationResponse(long id, String name, String companyName, String phoneNo, String email, String apiKey, boolean isActive, String password, Date createdAt) {
        this.id = id;
        this.name = name;
        this.companyName = companyName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.apiKey = apiKey;
        this.isActive = isActive;
        this.password = password;
        this.createdAt = createdAt;
    }

    public ClientRegistrationResponse(long id, String name, String companyName, String phoneNo, String email, String apiKey, boolean isActive, String password, Date createdAt,String accessToken) {
        this.id = id;
        this.name = name;
        this.companyName = companyName;
        this.phoneNo = phoneNo;
        this.email = email;
        this.apiKey = apiKey;
        this.isActive = isActive;
        this.password = password;
        this.createdAt = createdAt;
        this.accessToken = accessToken;

    }

    public ClientRegistrationResponse(long id, String name, String email, String phoneNo, String companyName, boolean active, Date createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.companyName = companyName;
        this.isActive = active;
        this.createdAt = createdAt;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
