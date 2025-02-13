package com.sanjeet.chat.sdk.controller;


import com.sanjeet.chat.sdk.model.dto.UserDetailsResponse;
import com.sanjeet.chat.sdk.model.entity.ClientUser;
import com.sanjeet.chat.sdk.service.UserService;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.HandleApiResponse;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sanjeet.chat.sdk.utils.Constant.INTERNAL_SERVER_ERROR;
import static com.sanjeet.chat.sdk.utils.Constant.SOME_ERROR_OCCURRED;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final HandleApiResponse handleApiResponse;

    @Autowired
    public UserController(UserService userService,HandleApiResponse handleApiResponse) {
        this.userService = userService;
        this.handleApiResponse = handleApiResponse;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ClientUser request) {
        try {
            UserDetailsResponse response = userService.registerUser(request);
            if (response == null){
                return handleApiResponse.handleApiFailedResponse(HttpStatus.BAD_REQUEST, Constant.FAILED_TO_REGISTER);
            }
            return handleApiResponse.handleApiSuccessResponse(HttpStatus.CREATED,"Success",response);
        }catch (CustomBusinessException e){
            logger.error(SOME_ERROR_OCCURRED + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR, SOME_ERROR_OCCURRED+ " " + e.getMessage());
        }
        catch (Exception e){
            logger.error(INTERNAL_SERVER_ERROR + "{}"  , e.getMessage());
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong "+e.getMessage());
        }
    }


}
