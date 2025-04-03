package com.sanjeet.chat.sdk.config;

import com.sanjeet.chat.sdk.model.entity.Admin;
import com.sanjeet.chat.sdk.model.entity.Client;
import com.sanjeet.chat.sdk.model.entity.ClientUser;
import com.sanjeet.chat.sdk.repository.AdminRepository;
import com.sanjeet.chat.sdk.repository.ClientRepository;
import com.sanjeet.chat.sdk.repository.UserRepository;
import com.sanjeet.chat.sdk.service.JwtService;
import com.sanjeet.chat.sdk.utils.Constant;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class TokenValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public TokenValidationFilter(JwtService jwtService, AdminRepository adminRepository, UserRepository userRepository, ClientRepository clientRepository) {
        this.jwtService = jwtService;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        System.out.println("Request URI: " + requestURI);
        if (isPublicUrl(requestURI)) {
            System.out.println("Public URL accessed, skipping validation: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        String token = authorizationHeader.substring(7);
        try {
            Claims claims = jwtService.extractAllClaims(token);
            System.out.println("request = " + request + ", response = " + response + ", claims = " + claims);
            String role = claims.get(Constant.ROLE, String.class);
            if (!role.isEmpty()) {
                System.out.println("Authorities: " + jwtService.getAuthorities(role));
                validateToken(token, claims, response);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                null, // Use API Key or token as the principal
                                null,   // No credentials
                                jwtService.getAuthorities(role) // Authorities based on role
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication set in SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or missing role");
                return;
            }
        } catch (Exception e) {
            System.out.println("Token validation failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token validation failed: " + e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void validateToken(String token, Claims claims, HttpServletResponse response) throws IOException {
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing Authorization token");
            throw new IllegalArgumentException("Invalid token in Authorization header");
        }
        String sessionTokenFromClaims = claims.get(Constant.CLAIM_SESSION_TOKEN, String.class);
        String role = claims.get(Constant.ROLE, String.class);
        String phone = claims.get(Constant.PHONE, String.class);
        String apiKey = claims.get(Constant.API_KEY, String.class);
        if (sessionTokenFromClaims == null || sessionTokenFromClaims.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing session token in claims");
            throw new IllegalArgumentException("Invalid session token in claims");
        }
        String email = claims.getSubject();
        if (role.equals(Constant.ADMIN)){
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
            String storedSessionToken = admin.getAccessToken();
            if (!token.equals(storedSessionToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Authorization token mismatch for ADMIN ");
                throw new IllegalArgumentException("Invalid token");
            }
        }else if (role.equals(Constant.CLIENT)){
            Client client = clientRepository.findByEmailAndApiKey(email,apiKey)
                    .orElseThrow(() -> new IllegalArgumentException("Client not found"));
            String storedSessionToken = client.getAccessToken();
            if (!token.equals(storedSessionToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Authorization token mismatch for CLIENT ");
                throw new IllegalArgumentException("Invalid token");
            }
        }else if (role.equals(Constant.USER)){
            ClientUser user = userRepository.findByPhoneNumber(phone,apiKey)
                    .orElseThrow(() -> new IllegalArgumentException("USER not found"));
            String storedSessionToken = user.getUserAccessToken();
            if (!token.equals(storedSessionToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Authorization token mismatch for USER ");
                throw new IllegalArgumentException("Invalid token");
            }
        }
        System.out.println("Admin session token validated successfully");
    }

    private boolean isPublicUrl(String requestURI) {
        return Arrays.stream(Constant.PUBLIC_URLS).anyMatch(requestURI::startsWith);
    }

}

