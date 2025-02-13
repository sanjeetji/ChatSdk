package com.sanjeet.chat.sdk.repository;

import com.sanjeet.chat.sdk.model.entity.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<ClientUser, Long> {

    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phone_number AND u.apiKey = :api_key")
    Optional<ClientUser> findByPhoneNumber(@Param("phone_number") String phoneNumber, @Param("api_key") String apiKey);

}
