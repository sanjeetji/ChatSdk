package com.sanjeet.chat.sdk.controller;


import com.sanjeet.chat.sdk.model.Client;
import com.sanjeet.chat.sdk.model.dto.ClientLoginRequest;
import com.sanjeet.chat.sdk.model.dto.ClientRegistrationResponse;
import com.sanjeet.chat.sdk.service.ClientService;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.HandleApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.sanjeet.chat.sdk.utils.Constant.REGISTRATION_FAILED;
import static com.sanjeet.chat.sdk.utils.Constant.REGISTRATION_SUCCESS;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final ClientService clientService;
    private final HandleApiResponse handleApiResponse;
    private final AuthenticationProvider clientAuthenticationProvider;

    public ClientController(ClientService clientService,HandleApiResponse handleApiResponse
            ,@Qualifier("clientAuthenticationProvider")AuthenticationProvider clientAuthenticationProvider){
        this.clientService = clientService;
        this.handleApiResponse = handleApiResponse;
        this.clientAuthenticationProvider = clientAuthenticationProvider;
    }

    @GetMapping("/greeting")
    public String greeting(){
        return "Have a nice day Client..";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Client request){
        try {
            ClientRegistrationResponse response = clientService.register(request);
            return handleApiResponse.handleApiSuccessResponse(HttpStatus.CREATED,REGISTRATION_SUCCESS,response);
        }catch (Exception e){
            return handleApiResponse.handleApiFailedResponse(HttpStatus.BAD_REQUEST,REGISTRATION_FAILED+ " : " +e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody ClientLoginRequest request){
        try {
            Authentication authentication = clientAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                ClientRegistrationResponse response = clientService.verifyClient(request);
                return handleApiResponse.handleApiSuccessResponse(HttpStatus.OK, Constant.LOGIN_SUCCESS, response);
            }else {
                return handleApiResponse.handleApiFailedResponse(HttpStatus.UNAUTHORIZED,  Constant.YOU_ARE_NOT_AUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR,Constant.YOU_ARE_NOT_AUTHORIZED+ " " + e.getMessage());
        }
    }

}
