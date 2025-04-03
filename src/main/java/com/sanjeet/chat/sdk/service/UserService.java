package com.sanjeet.chat.sdk.service;


import com.sanjeet.chat.sdk.model.dto.UserDetailsResponse;
import com.sanjeet.chat.sdk.model.entity.Client;
import com.sanjeet.chat.sdk.model.entity.ClientUser;
import com.sanjeet.chat.sdk.repository.UserRepository;
import com.sanjeet.chat.sdk.utils.ValidateInputs;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final ValidateInputs validateInputs;
    private final ClientService clientService;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,ValidateInputs validateInputs,ClientService clientService,JwtService jwtService){
        this.userRepository = userRepository;
        this.validateInputs = validateInputs;
        this.clientService = clientService;
        this.jwtService = jwtService;
    }

    public UserDetailsResponse registerUser(ClientUser request) throws Exception {
        try {
            validateInputs.handleUserRegistrationInput(request);
            Optional<Client> savedClient = clientService.findByApiKey(request.getApiKey());
            if (savedClient.isEmpty()) {
                throw new CustomBusinessException(ErrorCode.CLIENT_IS_NOT_FOUND_FOR_THE_GIVEN_API,HttpStatus.NOT_FOUND);
            }
            request.setClient(savedClient.get());
            Optional<ClientUser> userDetails = findByPhoneNumber(request.getPhoneNumber(),request.getApiKey());
            if (userDetails.isPresent()){
                throw new CustomBusinessException(ErrorCode.USER_IS_ALREADY_REGISTERED_WITH_THIS_PHONE,HttpStatus.CONFLICT);
            }
            if (request.getCreatedAt() == null) {
                request.setCreatedAt(new Date());
            }
            ClientUser user = userRepository.save(request);
            String userAccessToken = jwtService.generateUserAccessToken(
                    String.valueOf(savedClient.get().getId()),
                    String.valueOf(user.getId()),
                    String.valueOf(savedClient.get().getApiKey()),
                    request.getPhoneNumber(),
                    request.getUsername());
            updateUserSessionToken(user, userAccessToken);
            user.setUserAccessToken(userAccessToken);
            return new UserDetailsResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getUserImage(),
                    user.getPhoneNumber(),
                    user.getApiKey(),
                    user.getUserAccessToken(),
                    user.getCreatedAt());
        } catch (CustomBusinessException e) {
            logger.warn("Business Exception: {}", e.getMessage());
            throw e;
        }
        catch (Exception e){
            logger.error("Unexpected error while registering user", e);
            throw new Exception(e.getMessage());
        }
    }

    public void updateUserSessionToken(ClientUser user, String userAccessToken) {
        if (user == null) {
            throw new IllegalArgumentException("Client not found");
        }
        user.setUserAccessToken(userAccessToken);
        userRepository.save(user);
    }

    public Optional<ClientUser> findByPhoneNumber(String receiverPhone, String apiKey) {
        return userRepository.findByPhoneNumber(receiverPhone,apiKey);
    }

}
