package com.sanjeet.chat.sdk.config;

import com.sanjeet.chat.sdk.service.AdminServiceDetails;
import com.sanjeet.chat.sdk.service.ClientServiceDetails;
import com.sanjeet.chat.sdk.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final TokenValidationFilter tokenValidationFilter;
    private final ClientServiceDetails clientServiceDetails;
    private final AdminServiceDetails adminServiceDetails;

    @Autowired
    public SecurityConfig(TokenValidationFilter tokenValidationFilter, ClientServiceDetails clientServiceDetails,AdminServiceDetails adminServiceDetails) {
        this.tokenValidationFilter = tokenValidationFilter;
        this.clientServiceDetails = clientServiceDetails;
        this.adminServiceDetails = adminServiceDetails;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> {
                    request.requestMatchers(Constant.PUBLIC_URLS).permitAll();
                    request.requestMatchers("/api/v1/admin/**").hasRole(Constant.ADMIN);
                    request.requestMatchers("/api/v1/client/**").hasRole(Constant.CLIENT);
                    request.requestMatchers("/api/v1/user/**").hasRole(Constant.USER);
                    request.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenValidationFilter, UsernamePasswordAuthenticationFilter.class) // Add token validation filter
                .build();
    }

    @Bean
    public AuthenticationProvider clientAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(clientServiceDetails);
        return provider;
    }

    @Bean
    public AuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(adminServiceDetails);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return new ProviderManager(Arrays.asList(adminAuthenticationProvider(), clientAuthenticationProvider()));
    }

}
