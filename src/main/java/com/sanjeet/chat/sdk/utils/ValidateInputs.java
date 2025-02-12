package com.sanjeet.chat.sdk.utils;


import com.sanjeet.chat.sdk.model.Admin;
import com.sanjeet.chat.sdk.model.Client;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ValidateInputs {

    public void handleAdminRegistrationInput(Admin request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (request.getPhoneNo() == null || request.getPhoneNo().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
    }

    public void handleClientRegistrationInput(Client request){

        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (request.getCompanyName() == null || request.getCompanyName().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be null or empty");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (request.getPhoneNo() == null || request.getPhoneNo().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    }

    public String extractToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Authorization header is missing or invalid");
        }
        return authorizationHeader.substring(7);
    }

    public String extractTokenFromAuthHeader(String authorizationHeader){
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Authorization header is missing or invalid");
        }
        return authorizationHeader.substring(7);
    }

    public String extractApiKey(HttpServletRequest request){
        String apiKey = request.getHeader("API_KEY");
        if (apiKey == null) {
            throw new AccessDeniedException("API_KEY header is missing");
        }
        return apiKey;
    }
}
