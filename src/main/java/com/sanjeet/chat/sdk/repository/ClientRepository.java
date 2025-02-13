package com.sanjeet.chat.sdk.repository;

import com.sanjeet.chat.sdk.model.entity.Client;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT c FROM Client c WHERE c.isActive = :isActive")
    List<Client> findAllActiveClients(@Param("isActive") boolean isActive);

    Optional<Client> findByApiKey(String apiKey);

    @Modifying
    @Transactional
    @Query("UPDATE Client c SET c.isActive = :isActive WHERE c.id = :id")
    int updateClientStatus(@Param("isActive") boolean isActive,@Param("id") long id);


}
