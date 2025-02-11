package com.sanjeet.chat.sdk.service;

import com.sanjeet.chat.sdk.model.Admin;
import com.sanjeet.chat.sdk.repository.AdminRepository;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.ErrorCode;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceDetails implements UserDetailsService {

    private final AdminRepository adminRepository;

    public AdminServiceDetails(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Admin> admin = adminRepository.findByEmail(username);

        if (admin.isEmpty()){
            throw new CustomBusinessException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return User.builder()
                .username(admin.get().getEmail())
                .password(admin.get().getPassword())
                .roles(Constant.ADMIN)
                .build();
    }
}
