package com.sanjeet.chat.sdk.controller;


import com.sanjeet.chat.sdk.model.Admin;
import com.sanjeet.chat.sdk.model.dto.AdminLoginRequest;
import com.sanjeet.chat.sdk.model.dto.AdminRegistrationResponse;
import com.sanjeet.chat.sdk.service.AdminService;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.HandleApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final HandleApiResponse handleApiResponse;
    private final AuthenticationProvider adminAuthenticationProvider;

    public AdminController(AdminService adminService, HandleApiResponse handleApiResponse,
                           @Qualifier("adminAuthenticationProvider") AuthenticationProvider adminAuthenticationProvider){
        this.adminService = adminService;
        this.handleApiResponse = handleApiResponse;
        this.adminAuthenticationProvider = adminAuthenticationProvider;
    }

    @GetMapping("greeting")
    public String greeting(){
        return "Have a nice day admin...";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody Admin request) {
        try {
            AdminRegistrationResponse response = adminService.register(request);
            return handleApiResponse.handleApiSuccessResponse(HttpStatus.CREATED,"Congratulation Admin, You have register successful.",response);
        }catch (DataIntegrityViolationException e) {
            return handleApiResponse.handleApiFailedResponse(HttpStatus.BAD_REQUEST,e.getMessage() +"  ,User is already registered with this email: " + request.getEmail());
        }
        catch (Exception e){
            return handleApiResponse.handleApiFailedResponse(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginRequest request){
        try {
            System.out.println("Admin login Request = " + request);
            Authentication authentication = adminAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                AdminRegistrationResponse response = adminService.verifyUser(request);
                return handleApiResponse.handleApiSuccessResponse(HttpStatus.OK,Constant.LOGIN_SUCCESS, response);
            }else {
                return handleApiResponse.handleApiFailedResponse(HttpStatus.UNAUTHORIZED,  Constant.YOU_ARE_NOT_AUTHORIZED);
            }
        }catch (Exception e){
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR,Constant.YOU_ARE_NOT_AUTHORIZED+ " " + e.getMessage());
        }

    }


}
