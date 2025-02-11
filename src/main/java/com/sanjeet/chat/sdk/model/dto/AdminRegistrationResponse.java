package com.sanjeet.chat.sdk.model.dto;


import java.util.Date;

public class AdminRegistrationResponse {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNo;
    private String accessToken;
    private Date createdAt;


    public AdminRegistrationResponse() {
    }

    public AdminRegistrationResponse(Long id, String name, String email, String phoneNo, Date createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.createdAt = createdAt;
    }

    public AdminRegistrationResponse(Long id, String name, String email, String password, String phoneNo, String accessToken, Date createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.accessToken = accessToken;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
