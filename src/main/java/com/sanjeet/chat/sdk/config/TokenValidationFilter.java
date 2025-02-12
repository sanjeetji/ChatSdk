package com.sanjeet.chat.sdk.config;

import com.sanjeet.chat.sdk.model.Admin;
import com.sanjeet.chat.sdk.model.Client;
import com.sanjeet.chat.sdk.model.ClientUser;
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
    public TokenValidationFilter(JwtService jwtService, AdminRepository adminRepository,
                                 UserRepository userRepository, ClientRepository clientRepository) {
        this.jwtService = jwtService;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        System.out.println("Request URI: " + requestURI);
        if (isPublicUrl(requestURI)) {
            System.out.println("Public URL accessed, skipping validation: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        String authorizationHeader = request.getHeader("Authorization");
        String apiKey = request.getHeader("API_KEY");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        String token = authorizationHeader.substring(7);
        if (apiKey == null){
            try {
                Claims claims = jwtService.extractAllClaims(token);
                System.out.println("request = " + request + ", response = " + response + ", claims = " + claims);
                String role = claims.get(Constant.ROLE, String.class);
                if (Constant.ADMIN.equals(role)) {
                    System.out.println("Authorities: " + jwtService.getAuthorities(role));
                    validateAdminToken(token, claims, response);
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
        }else {
            try {
                var claims = jwtService.validateTokenAndGetClaims(token, apiKey);
                String role = claims.get(Constant.ROLE, String.class);
                if (Constant.ADMIN.equals(role)) {
                    validateAdminToken(token, claims, response);
                } else if (Constant.CLIENT.equals(role) || Constant.USER.equals(role)) {
                    validateClientOrUserToken(token, apiKey, claims, response,role);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or missing role");
                    return;
                }
                System.out.println("request = 111 " + request + ", response = " + response + ", filterChain = " + filterChain);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                apiKey, // Use API Key or token as the principal
                                null,   // No credentials
                                jwtService.getAuthorities(role) // Authorities based on role
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Authentication set in SecurityContext: 111 " + SecurityContextHolder.getContext().getAuthentication());
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token validation failed: " + e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
        System.out.println("Token validated successfully, passing to the controller...");
    }

    private void validateAdminToken(String token, Claims claims, HttpServletResponse response) throws IOException {
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing Authorization token");
            throw new IllegalArgumentException("Invalid token in Authorization header");
        }
        String sessionTokenFromClaims = claims.get(Constant.CLAIM_SESSION_TOKEN, String.class);
        if (sessionTokenFromClaims == null || sessionTokenFromClaims.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing session token in claims");
            throw new IllegalArgumentException("Invalid session token in claims");
        }
        String email = claims.getSubject();
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        String storedSessionToken = admin.getAccessToken();
        if (!token.equals(storedSessionToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization token mismatch for ADMIN");
            throw new IllegalArgumentException("Invalid token");
        }
        System.out.println("Admin session token validated successfully");
    }

    private void validateClientOrUserToken(String token, String apiKey, Claims claims, HttpServletResponse response, String role) throws IOException {
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing Authorization token");
            throw new IllegalArgumentException("Invalid token in Authorization header");
        }
        String tokenApiKey = claims.get(Constant.API_KEY, String.class); // Extract API Key from claims
        String sessionTokenFromClaims = claims.get(Constant.CLAIM_SESSION_TOKEN, String.class);
        if (sessionTokenFromClaims == null || sessionTokenFromClaims.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid session token for CLIENT/USER");
            throw new IllegalArgumentException("Invalid session token");
        }
        if (!apiKey.equals(tokenApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API Key or mismatched token for CLIENT/USER");
            throw new IllegalArgumentException("API Key mismatch");
        }
        if (role.equals(Constant.CLIENT)){
            String id = claims.getSubject();
            Client client = clientRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new IllegalArgumentException("Client not found"));
            String storedSessionToken = client.getAccessToken();
            if (!token.equals(storedSessionToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Authorization token mismatch for ADMIN");
                throw new IllegalArgumentException("Invalid token");
            }

        }else if (role.equals(Constant.USER)){
            String id = claims.getSubject();
            ClientUser user = userRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
            String storedSessionToken = user.getUserAccessToken();
            if (!token.equals(storedSessionToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Authorization token mismatch for ADMIN");
                throw new IllegalArgumentException("Invalid token");
            }
        }
        System.out.println("Client/USER API Key and session token validated successfully");
    }

    private boolean isPublicUrl(String requestURI) {
        return Arrays.stream(Constant.PUBLIC_URLS).anyMatch(requestURI::startsWith);
    }

}

