package com.sanjeet.chat.sdk.utils;


import com.sanjeet.chat.sdk.model.entity.Admin;
import com.sanjeet.chat.sdk.model.entity.Client;
import com.sanjeet.chat.sdk.model.entity.ClientUser;
import com.sanjeet.chat.sdk.model.entity.Message;
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

    public void handleUserRegistrationInput(ClientUser request){

        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("User Phone cannot be null or empty");
        }
        if (request.getApiKey() == null || request.getApiKey().isEmpty()) {
            throw new IllegalArgumentException("API KEY cannot be null or empty");
        }
        if (request.getUserImage() == null || request.getUserImage().isEmpty()) {
            throw new IllegalArgumentException("User Image cannot be null or empty");
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

    public void handlePostMessageInput(Message request) {
        if (request.getReceiverPhone() == null || request.getReceiverPhone().isEmpty()) {
            throw new IllegalArgumentException("Receiver phone no cannot be null or empty");
        }
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
    }


}
