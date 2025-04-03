package com.sanjeet.chat.sdk.repository;

import com.sanjeet.chat.sdk.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c.secretKey FROM Client c WHERE c.apiKey = :apiKey")
    String findSecretKeyByApiKey(@Param("apiKey") String apiKey);

    Optional<Client> findByEmail(String email);

    @Query("SELECT c FROM Client c WHERE c.email =:email AND c.apiKey = :apiKey")
    Optional<Client> findByEmailAndApiKey(@Param("email") String email,@Param("apiKey") String apiKey);

    @Query("SELECT c FROM Client c WHERE c.isActive = :isActive")
    List<Client> findAllActiveClients(@Param("isActive") boolean isActive);

    @Query("SELECT c FROM Client c WHERE c.apiKey = :apiKey")
    Optional<Client> findByApiKey(@Param("apiKey") String apiKey);

    @Query("SELECT c FROM Client c WHERE c.apiKey = :apiKey AND c.isActive = :isActive")
    Optional<Client> findActiveClientByApiKey(@Param("apiKey") String apiKey,@Param("isActive") boolean isActive);


}
