package com.sanjeet.chat.sdk.repository;

import com.sanjeet.chat.sdk.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;


@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c.secretKey FROM client_details c WHERE c.apiKey = :apiKey")
    String findSecretKeyByApiKey(@Param("apiKey") String apiKey);

    Optional<Client> findByEmail(String email);

}
