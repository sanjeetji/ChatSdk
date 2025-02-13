package com.sanjeet.chat.sdk.service;

import com.sanjeet.chat.sdk.model.entity.Client;
import com.sanjeet.chat.sdk.model.dto.ClientLoginRequest;
import com.sanjeet.chat.sdk.model.dto.ClientRegistrationResponse;
import com.sanjeet.chat.sdk.repository.ClientRepository;
import com.sanjeet.chat.sdk.utils.SecretKeyGenerator;
import com.sanjeet.chat.sdk.utils.ValidateInputs;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

import static com.sanjeet.chat.sdk.utils.Constant.FAILED_TO_LOGIN;
import static com.sanjeet.chat.sdk.utils.Constant.H_MAC_ALGORITHM;

@Service
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private final ClientRepository clientRepository;
    private final ValidateInputs validateInputs;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ClientService(ClientRepository clientRepository,ValidateInputs validateInputs
    ,PasswordEncoder passwordEncoder,JwtService jwtService){
        this.clientRepository = clientRepository;
        this.validateInputs = validateInputs;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ClientRegistrationResponse register(Client request) {
        try {
            validateInputs.handleClientRegistrationInput(request);
            Optional<Client> savedClient = clientRepository.findByEmail(request.getEmail());
            if (savedClient.isPresent()){
                throw new CustomBusinessException(ErrorCode.USER_IS_ALREADY_REGISTER, HttpStatus.CONFLICT);
            }
            String secretKey = SecretKeyGenerator.generateKey();
            String apiKey = generateApiKey(request, secretKey);
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            if (request.getCreatedAt() == null) {
                request.setCreatedAt(new Date());
            }
            request.setSecretKey(secretKey);
            request.setApiKey(apiKey);
            request.setAccessToken("");
            request.setActive(true);
            Client response  = clientRepository.save(request);
            return new ClientRegistrationResponse(
                    response.getId(),
                    response.getName(),
                    response.getCompanyName(),
                    response.getPhoneNo(),
                    response.getEmail(),
                    response.getApiKey(),
                    response.isActive(),
                    response.getPassword(),
                    response.getCreatedAt());
        }catch (Exception e){
            throw new CustomBusinessException(ErrorCode.USER_IS_ALREADY_REGISTER, HttpStatus.CONFLICT);
        }

    }

    public String generateApiKey(Client request, String secretKey) {
        String input = request.getName() + request.getCompanyName() + request.getEmail() + request.getPhoneNo();
        String uuid = UUID.randomUUID().toString();
        try {
            Mac hmac = Mac.getInstance(H_MAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), H_MAC_ALGORITHM);
            hmac.init(secretKeySpec);
            byte[] hash = hmac.doFinal((input + uuid).getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating API key", e);
        }
    }

    public void updateClientSessionToken(Client client, String sessionToken) {
        if (client == null) {
            throw new IllegalArgumentException("Client not found");
        }
        client.setAccessToken(sessionToken);
        clientRepository.save(client);
    }

    public ClientRegistrationResponse verifyClient(ClientLoginRequest request) throws Exception {
        try {
            Optional<Client> client = clientRepository.findByEmail(request.getEmail());
            if (client.isEmpty()){
                throw  new CustomBusinessException(ErrorCode.USER_NOT_FOUND,HttpStatus.NOT_FOUND);
            }
            String sessionToken = jwtService.generateClientAccessToken(String.valueOf(client.get().getId()), client.get().getApiKey(),client.get().getEmail());
            updateClientSessionToken(client.get(), sessionToken);
            return new ClientRegistrationResponse(
                    client.get().getId(),
                    client.get().getName(),
                    client.get().getCompanyName(),
                    client.get().getPhoneNo(),
                    client.get().getEmail(),
                    client.get().getApiKey(),
                    client.get().isActive(),
                    client.get().getPassword(),
                    client.get().getCreatedAt(),
                    sessionToken);
        }catch (Exception e){
            throw  new Exception(FAILED_TO_LOGIN + " : " +e.getMessage());
        }
    }

   public List<ClientRegistrationResponse> findAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> new ClientRegistrationResponse(
                        client.getId(),
                        client.getName(),
                        client.getEmail(),
                        client.getPhoneNo(),
                        client.getCompanyName(),
                        client.isActive(),
                        client.getCreatedAt()
                ))
                .toList();
    }

    public List<Client> findAllActiveClients(boolean isActive) {
        return clientRepository.findAllActiveClients(isActive);
    }

    public Optional<Client> findByApiKey(String apiKey) {
        return clientRepository.findByApiKey(apiKey);
    }

    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public String updateClientStatus(Long id, boolean isActive) throws Exception {
        try {
            Client client = clientRepository.findById(id).orElseThrow(() -> new Exception("⚠️ No client found with ID: " + id));
            client.setActive(isActive); // ✅ Update active status
            clientRepository.save(client); // ✅ Save the updated entity
            return "Client status updated successfully for ID: " + id;
        } catch (Exception e) {
            logger.error("Error updating client status: {}", e.getMessage(), e);
            throw new Exception("Failed to update client status: " + e.getMessage());
        }
    }

}
