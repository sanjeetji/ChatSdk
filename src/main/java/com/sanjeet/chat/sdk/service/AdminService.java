package com.sanjeet.chat.sdk.service;

import com.sanjeet.chat.sdk.model.entity.Admin;
import com.sanjeet.chat.sdk.model.dto.AdminLoginRequest;
import com.sanjeet.chat.sdk.model.dto.AdminRegistrationResponse;
import com.sanjeet.chat.sdk.repository.AdminRepository;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.ValidateInputs;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.ErrorCode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final ValidateInputs validateInputs;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminService(AdminRepository adminRepository,ValidateInputs validateInputs, PasswordEncoder passwordEncoder,JwtService jwtService){
        this.adminRepository = adminRepository;
        this.validateInputs = validateInputs;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AdminRegistrationResponse register(Admin request) throws Exception{
        try {
            validateInputs.handleAdminRegistrationInput(request);
            Optional<Admin> savedAdmin = adminRepository.findByEmail(request.getEmail());
            if (savedAdmin.isPresent()){
                throw new CustomBusinessException(ErrorCode.USER_IS_ALREADY_REGISTER, HttpStatus.CONFLICT);
            }
            request.setPassword(passwordEncoder.encode(request.getPassword()));
            if (request.getCreatedAt() == null) {
                request.setCreatedAt(new Date());
            }
            request.setAccessToken(Constant.EMPTY_STRING);
            Admin admin =  adminRepository.save(request);
            return new AdminRegistrationResponse(admin.getId(),admin.getName(),admin.getEmail(),admin.getPhoneNo(),admin.getCreatedAt());
        }catch (Exception e){
            throw new Exception(Constant.FAILED_TO_REGISTER +" "+e.getMessage());
        }
    }

    public AdminRegistrationResponse verifyUser(@Valid AdminLoginRequest request) throws Exception {
        try {
            Optional<Admin> admin = findByEmail(request.getEmail());
            if (admin.isEmpty()){
                throw new CustomBusinessException(ErrorCode.FAILED_TO_LOGIN,HttpStatus.NOT_FOUND);
            }
            String accessToken = jwtService.generateAdminAccessToken(admin.get());
            updateAdminAccessToken(admin.get(), accessToken);
            return new AdminRegistrationResponse(
                    admin.get().getId(), admin.get().getName(), admin.get().getEmail(),
                    admin.get().getPassword(), admin.get().getPhoneNo(), accessToken, admin.get().getCreatedAt());
        }catch (Exception e){
            throw new Exception(Constant.FAILED_TO_LOGIN + " : " +e.getMessage());
        }
    }

    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public void updateAdminAccessToken(Admin admin, String accessToken) {
        admin.setAccessToken(accessToken);
        adminRepository.save(admin);
    }


}
