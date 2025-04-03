package com.sanjeet.chat.sdk.controller;


import com.sanjeet.chat.sdk.model.dto.MessageDetailsResponse;
import com.sanjeet.chat.sdk.model.entity.Message;
import com.sanjeet.chat.sdk.service.ChatService;
import com.sanjeet.chat.sdk.service.UserService;
import com.sanjeet.chat.sdk.utils.HandleApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final HandleApiResponse handleApiResponse;

    @Autowired
    public ChatController(ChatService chatService, UserService userService, HandleApiResponse handleApiResponse) {
        this.chatService = chatService;
        this.handleApiResponse = handleApiResponse;
    }

    @PostMapping("/message")
    public ResponseEntity<?> postChat(@RequestBody Message data, HttpServletRequest request) {
        try {
            MessageDetailsResponse response = chatService.postChat(data,request);
            return handleApiResponse.handleApiSuccessResponse(HttpStatus.OK,"Message Post Successful.",response);
        } catch (Exception e) {
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<?> fetchChat(@RequestParam("receiverPhone") String receiverPhone,@RequestParam("apiKey") String apiKey, HttpServletRequest request) {
        try {
            List<MessageDetailsResponse> response = chatService.fetchChat(receiverPhone,apiKey,request);
            return handleApiResponse.handleApiSuccessResponse(HttpStatus.OK,"Message fetched successful.",response);
        } catch (Exception e) {
            return handleApiResponse.handleApiFailedResponse(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

}