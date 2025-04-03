package com.sanjeet.chat.sdk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanjeet.chat.sdk.model.entity.Admin;
import com.sanjeet.chat.sdk.repository.ClientRepository;
import com.sanjeet.chat.sdk.utils.Constant;
import com.sanjeet.chat.sdk.utils.SecretKeyGenerator;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.CustomBusinessException;
import com.sanjeet.chat.sdk.utils.globalExceptionHandller.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

import static com.sanjeet.chat.sdk.utils.Constant.*;


@Service
public class JwtService {

    private static final long EXPIRATION_90_DAYS = 1000L * 60 * 60 * 24 * 90;
    private final ClientRepository clientRepository;

    public JwtService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public String generateClientAccessToken(String clientId, String apiKey,String email) {
        String secretKey = clientRepository.findSecretKeyByApiKey(apiKey);
        if (secretKey == null) {
            throw new IllegalArgumentException("Invalid API Key");
        }
        SecretKey key = getSigningKey();
        String claimSessionToken = UUID.randomUUID().toString(); // Generate claimSession token
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLE,Constant.CLIENT);
        claims.put(USER_NAME,email);
        claims.put(API_KEY,apiKey);
        claims.put(CLIENT_ID,clientId);
        claims.put(CLAIM_SESSION_TOKEN,claimSessionToken);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(clientId) // Use clientId as the
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_90_DAYS))
                .and()
                .signWith(key)
                .compact();
    }

    public String generateUserAccessToken(String clientId, String userId, String apiKey,String phoneNo,String userName) {
        String secretKey = clientRepository.findSecretKeyByApiKey(apiKey);
        if (secretKey == null) {
            throw new IllegalArgumentException("Invalid API Key");
        }
        Key key = getSigningKey();
        System.out.println("🔑 Signing Key (USER) = " + key);
        String claimSessionToken = UUID.randomUUID().toString(); // Generate claimSession token
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLE,Constant.USER);
        claims.put(USER_NAME,userName);
        claims.put(PHONE,phoneNo);
        claims.put(CLIENT_ID,clientId);
        claims.put(API_KEY,apiKey);
        claims.put(CLAIM_SESSION_TOKEN,claimSessionToken);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_90_DAYS))
                .and()
                .signWith(key)
                .compact();
    }

    public String generateAdminAccessToken(Admin admin) {
        System.out.println("Generating JWT for email: " + admin.getEmail());
        String claimSessionToken = UUID.randomUUID().toString(); // Generate claimSession token
        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLE,Constant.ADMIN);
        claims.put(USER_NAME,admin.getName());
        claims.put(PHONE,admin.getPhoneNo());
        claims.put(Constant.CLAIM_SESSION_TOKEN,claimSessionToken);
        Key key = getSigningKey();
        System.out.println("🔑 Signing Key (ADMIN) = " + key);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(admin.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_90_DAYS))
                .and()
                .signWith(key)
                .compact();
    }

    public Claims validateTokenAndGetClaims(String token, String apiKey) throws Exception {
        try {
            SecretKey key;
            if (apiKey != null) {
                key = getSigningKey();
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

    public Claims extractAllClaims(String token) {

        // Step 1: Extract the role without verification
        String extractedRole = extractRoleFromToken(token);
        System.out.println("🔍 Extracted Role from Token: " + extractedRole);

        // Step 2: Fetch the correct key based on extracted role
        SecretKey key = getSigningKey();
        System.out.println("🔑 Using Signing Key for Role: " + extractedRole);

//        SecretKey key = getSigningKey("ADMIN"); // ✅ Now returns a SecretKey
//        System.out.println("🔑 Extracting Claims - Signing Key (ADMIN): " + key);

        return Jwts.parser()
                .verifyWith(key) // ✅ Now it will work because key is a SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String extractRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\."); // Split JWT: Header, Payload, Signature
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT Token");
            }

            // Decode the payload (middle part of JWT)
            String payloadJson = new String(Base64.getDecoder().decode(parts[1]));
            System.out.println("🔍 Decoded JWT Payload: " + payloadJson);

            // Convert JSON to Map and extract role
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> claims = objectMapper.readValue(payloadJson, Map.class);
            return (String) claims.get(Constant.ROLE); // Extract role

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract role from token", e);
        }
    }

    public Collection<GrantedAuthority> getAuthorities(String role) {
        return List.of(() -> "ROLE_"+role); // Assign role as GrantedAuthority
    }

    private SecretKey getSigningKey() {
        String secret = SecretKeyGenerator.getSecret();
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("❌ JWT_SECRET not set");
        }
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }


}
