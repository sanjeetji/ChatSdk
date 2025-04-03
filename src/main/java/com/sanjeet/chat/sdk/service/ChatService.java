package com.sanjeet.chat.sdk.service;


import com.sanjeet.chat.sdk.model.dto.MessageDetailsResponse;
import com.sanjeet.chat.sdk.model.entity.Client;
import com.sanjeet.chat.sdk.model.entity.ClientUser;
import com.sanjeet.chat.sdk.model.entity.Message;
import com.sanjeet.chat.sdk.repository.ChatRepository;
import com.sanjeet.chat.sdk.repository.ClientRepository;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.ValidateInputs;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ClientRepository clientRepository;
    private final ValidateInputs validateInputs;
    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public ChatService(ChatRepository chatRepository, ClientRepository clientRepository, ValidateInputs validateInputs, JwtService jwtService, UserService userService){
        this.chatRepository = chatRepository;
        this.clientRepository = clientRepository;
        this.validateInputs = validateInputs;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public MessageDetailsResponse postChat(Message messageRequest, HttpServletRequest request) throws Exception {
        try {
            validateInputs.handlePostMessageInput(messageRequest);
            Optional<Client> client = clientRepository.findActiveClientByApiKey(messageRequest.getApiKey(),true);
            if (client.isEmpty()){
                throw new CustomBusinessException(ErrorCode.CLIENT_IS_NOT_ACTIVE,HttpStatus.NOT_FOUND);
            }
            Optional<ClientUser> user = userService.findByPhoneNumber(messageRequest.getSenderPhone(), messageRequest.getApiKey());
            if (client.isEmpty()){
                throw new CustomBusinessException(ErrorCode.USER_IS_NOT_FOUND,HttpStatus.NOT_FOUND);
            }
            Optional<ClientUser> receiver = userService.findByPhoneNumber(messageRequest.getReceiverPhone(), messageRequest.getApiKey());
            if (receiver.isEmpty()){
                throw new CustomBusinessException(ErrorCode.RECEIVER_NOT_FOUND,HttpStatus.NOT_FOUND);
            }
            if (messageRequest.getReceiverPhone().equals(user.get().getPhoneNumber())){
                throw new CustomBusinessException(ErrorCode.SENDER_RECEIVER_PHONE_NO_CAN_NOT_BE_SAME,HttpStatus.CONFLICT);
            }
            String rPhone = messageRequest.getReceiverPhone();
            String sPhone = user.get().getPhoneNumber();
            if (!rPhone.startsWith("+")) {
                rPhone = "+" + rPhone;
            }
            if (!sPhone.startsWith("+")) {
                sPhone = "+" + sPhone;
            }
            if (messageRequest.getCreatedAt() == null) {
                messageRequest.setCreatedAt(new Date());
            }
            messageRequest.setSenderPhone(sPhone);
            messageRequest.setReceiverPhone(rPhone);
            Message response = chatRepository.save(messageRequest);
            return new MessageDetailsResponse(response.getId(),response.getMessage());
        }catch (Exception e){
            String message = "Failed to post chat message" + e;
            throw new Exception(message);
        }
    }

    public List<MessageDetailsResponse> fetchChat(String receiverPhone,String apiKey, HttpServletRequest request) throws Exception {
        try {
            String sessionToken = validateInputs.extractToken(request);
            Claims claims = jwtService.validateTokenAndGetClaims(sessionToken,apiKey);
            String senderPhone = claims.get(Constant.PHONE,String.class);


            Optional<Client> client = clientRepository.findActiveClientByApiKey(apiKey,true);
            if (client.isEmpty()){
                throw new CustomBusinessException(ErrorCode.CLIENT_IS_NOT_ACTIVE,HttpStatus.NOT_FOUND);
            }

            Optional<ClientUser> user = userService.findByPhoneNumber(senderPhone, apiKey);
            if (user.isEmpty()){
                throw new CustomBusinessException(ErrorCode.USER_IS_NOT_FOUND,HttpStatus.NOT_FOUND);
            }
            Optional<ClientUser> receiver = userService.findByPhoneNumber(receiverPhone, apiKey);
            if (receiver.isEmpty()){
                throw new CustomBusinessException(ErrorCode.RECEIVER_NOT_FOUND,HttpStatus.NOT_FOUND);
            }

            receiverPhone = receiverPhone.replaceAll("\\s+", ""); // remove all spaces
            if (!receiverPhone.startsWith("+")) {
                receiverPhone = "+" + receiverPhone;
            }
            if (!senderPhone.startsWith("+")) {
                senderPhone = "+" + senderPhone;
            }
            if (senderPhone.equals(receiverPhone)){
                throw new CustomBusinessException(ErrorCode.SENDER_RECEIVER_PHONE_NO_CAN_NOT_BE_SAME,HttpStatus.CONFLICT);
            }
            System.out.println("Using phone: [" + receiverPhone + "], apiKey: [" + apiKey + "]");
            List<Message> savedMessages = chatRepository.findAllByPhoneNumber(receiverPhone,apiKey);
            return savedMessages
                    .stream()
                    .map(entity -> {
                        String message = entity.getMessage() != null ? entity.getMessage() : "No Message";
                        return new MessageDetailsResponse(entity.getId(), message);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Failed to fetch messages : "+e.getMessage(), e);
        }
    }


}
