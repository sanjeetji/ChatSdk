package com.sanjeet.chat.sdk.service;

import com.sanjeet.chat.sdk.model.Client;
import com.sanjeet.chat.sdk.repository.ClientRepository;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceDetails implements UserDetailsService {

    private final ClientRepository clientRepository;

    public ClientServiceDetails(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("🔍 Checking Client login for email: " + username);
        Optional<Client> client = clientRepository.findByEmail(username);
        if (client.isEmpty()) {
            System.out.println("❌ Client not found: " + username);
            throw new CustomBusinessException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        System.out.println("✅ Client authenticated: " + client.get().getEmail());
        return User.builder()
                .username(client.get().getEmail())
                .password(client.get().getPassword())
                .roles(Constant.CLIENT) // Ensure the correct role is assigned
                .build();
    }
}
