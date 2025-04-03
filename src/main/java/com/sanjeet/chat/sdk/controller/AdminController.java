package com.sanjeet.chat.sdk.controller;


import com.sanjeet.chat.sdk.model.dto.ClientRegistrationResponse;
import com.sanjeet.chat.sdk.model.entity.Admin;
import com.sanjeet.chat.sdk.model.entity.Client;
import com.sanjeet.chat.sdk.model.dto.AdminLoginRequest;
import com.sanjeet.chat.sdk.model.dto.AdminRegistrationResponse;
import com.sanjeet.chat.sdk.service.AdminService;
import com.sanjeet.chat.sdk.service.ClientService;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.HandleApiResponse;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.sanjeet.chat.sdk.utils.Constant.INTERNAL_SERVER_ERROR;
import static com.sanjeet.chat.sdk.utils.Constant.SOME_ERROR_OCCURRED;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final HandleApiResponse handleApiResponse;
    private final AuthenticationProvider adminAuthenticationProvider;
    private final ClientService clientService;

    public AdminController(AdminService adminService, HandleApiResponse handleApiResponse, @Qualifier("adminAuthenticationProvider") AuthenticationProvider adminAuthenticationProvider,ClientService clientService){
        this.adminService = adminService;
        this.handleApiResponse = handleApiResponse;
        this.adminAuthenticationProvider = adminAuthenticationProvider;
        this.clientService = clientService;
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
            logger.error(SOME_ERROR_OCCURRED + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.BAD_REQUEST,e.getMessage() +"  ,User is already registered with this email: " + request.getEmail());
        }
        catch (CustomBusinessException e){
            logger.error(SOME_ERROR_OCCURRED + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR, SOME_ERROR_OCCURRED+ " " + e.getMessage());
        }
        catch (Exception e){
            logger.error(INTERNAL_SERVER_ERROR + "{}"  , e.getMessage());
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
        }catch (CustomBusinessException e){
            logger.error(SOME_ERROR_OCCURRED + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR, SOME_ERROR_OCCURRED+ " " + e.getMessage());
        }
        catch (Exception e){
            logger.error(INTERNAL_SERVER_ERROR + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR,Constant.YOU_ARE_NOT_AUTHORIZED+ " " + e.getMessage());
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<?> getClients(){
        try {
            List<ClientRegistrationResponse> responses = clientService.findAllClients();
            if (responses == null || responses.isEmpty()) {
                return handleApiResponse.handleApiFailedResponse(HttpStatus.NOT_FOUND,"Client details not found");
            }
            return handleApiResponse.handleApiSuccessResponse(HttpStatus.OK,"Client details fetched successfully.",responses);
        }catch (CustomBusinessException e){
            logger.error(SOME_ERROR_OCCURRED + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR, SOME_ERROR_OCCURRED+ " " + e.getMessage());
        }
        catch (Exception e){
            logger.error(INTERNAL_SERVER_ERROR + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR,Constant.YOU_ARE_NOT_AUTHORIZED+ " " + e.getMessage());
        }
    }

    @GetMapping("/active-clients")
    public ResponseEntity<?> getActiveClients(@RequestParam("is_active") boolean isActive){
        try {
            List<Client> clientList = clientService.findAllActiveClients(isActive);
            if (clientList == null || clientList.isEmpty()) {
                return handleApiResponse.handleApiFailedResponse(HttpStatus.NOT_FOUND,"Client details not found");
            }
            return handleApiResponse.handleApiSuccessResponse(HttpStatus.OK,"Client details fetched successfully.",clientList);
        }catch (CustomBusinessException e){
            logger.error(SOME_ERROR_OCCURRED + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR, SOME_ERROR_OCCURRED+ " " + e.getMessage());
        }
        catch (Exception e){
            logger.error(INTERNAL_SERVER_ERROR + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR,Constant.YOU_ARE_NOT_AUTHORIZED+ " " + e.getMessage());
        }
    }

    @PutMapping("/update-client-status")
    public ResponseEntity<?> changeClientStatus(@RequestParam Long id, @RequestParam boolean isActive){
        try {
            String message = clientService.updateClientStatus(id,isActive);
            return handleApiResponse.handleApiSuccessResponse(HttpStatus.OK,Constant.SUCCESS,message);
        }catch (CustomBusinessException e){
            logger.error(SOME_ERROR_OCCURRED + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (Exception e){
            logger.error(INTERNAL_SERVER_ERROR+ "{}", e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


}
