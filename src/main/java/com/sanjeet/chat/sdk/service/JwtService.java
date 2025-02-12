package com.sanjeet.chat.sdk.service;

import com.sanjeet.chat.sdk.model.Admin;
import com.sanjeet.chat.sdk.repository.AdminRepository;
import com.sanjeet.chat.sdk.repository.ClientRepository;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.sanjeet.chat.sdk.utils.Constant.H_MAC_ALGORITHM;


@Service
public class JwtService {

    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;
    private String secretKey = "";


    public JwtService(ClientRepository clientRepository, AdminRepository adminRepository) {
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
    }

    public String generateClientAccessToken(String clientId, String apiKey,String email) {
        String secretKey = clientRepository.findSecretKeyByApiKey(apiKey);
        if (secretKey == null) {
            throw new IllegalArgumentException("Invalid API Key");
        }
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String claimSessionToken = UUID.randomUUID().toString(); // Generate session token
        long expirationTime = 1000 * 60 * 60 * 24; // Token valid for 24 hours
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constant.ROLE,Constant.CLIENT);
        claims.put(Constant.USER_NAME,email);
        claims.put(Constant.API_KEY,apiKey);
        claims.put(Constant.CLIENT_ID,clientId);
        claims.put(Constant.CLAIM_SESSION_TOKEN,claimSessionToken);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(clientId) // Use clientId as the
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .and()
                .signWith(key)
                .compact();
    }

    public String generateUserAccessToken(String clientId, String userId, String apiKey,String phoneNo) {
        String secretKey = clientRepository.findSecretKeyByApiKey(apiKey);
        if (secretKey == null) {
            throw new IllegalArgumentException("Invalid API Key");
        }
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
        String claimSessionToken = UUID.randomUUID().toString(); // Generate session token
        long expirationTime = 1000 * 60 * 60; // 1 hour for user tokens
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constant.ROLE,Constant.USER);
        claims.put(Constant.USER_NAME,phoneNo);
        claims.put(Constant.CLIENT_ID,clientId);
        claims.put(Constant.API_KEY,apiKey);
        claims.put(Constant.CLAIM_SESSION_TOKEN,claimSessionToken);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .and()
                .signWith(key)
                .compact();
    }

    public String generateAdminAccessToken(String email) {
        System.out.println("Generating JWT for email: " + email);
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        Optional<Admin> registerAdmin = adminRepository.findByEmail(email);
        if (registerAdmin.isEmpty()){
            throw new CustomBusinessException(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        initializeAdminSecretKey();
        String claimSessionToken = UUID.randomUUID().toString(); // Generate session token
        long expirationTime = 1000 * 60 * 60 * 24; // Token valid for 24 hours
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constant.ROLE,Constant.ADMIN);
        claims.put(Constant.USER_NAME,email);
        claims.put(Constant.CLAIM_SESSION_TOKEN,claimSessionToken);
        System.out.println("key = At generating admin session token 1 " + getKey() + " And Secret Key is : "+secretKey);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .and()
                .signWith(getKey())
                .compact();
    }

    private void initializeAdminSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(H_MAC_ALGORITHM);
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Claims validateTokenAndGetClaims(String token, String apiKey) throws Exception {
        try {
            SecretKey key;
            if (apiKey != null) {
                String secretKey = clientRepository.findSecretKeyByApiKey(apiKey);
                if (secretKey == null) {
                    throw new IllegalArgumentException("Invalid API Key");
                }
                key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
                return Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();
            }else {
                throw new CustomBusinessException(ErrorCode.FAILED_TO_EXTRACT_CLAIMS,HttpStatus.FORBIDDEN);
            }
        }catch (Exception e){
            throw new Exception(ErrorCode.FAILED_TO_EXTRACT_CLAIMS + " : "+e.getMessage());
        }
    }

    public Claims extractAllClaims(String token){
        secretKey = "V97UNlRha40+yUZ+jalePCZ84pwVWMisHfq1/2i7Tz4=";
        System.out.println("key = At extractAllClaims admin Key is " + getKey());
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public Collection<GrantedAuthority> getAuthorities(String role) {
        return List.of(() -> "ROLE_"+role); // Assign role as GrantedAuthority
    }



}
