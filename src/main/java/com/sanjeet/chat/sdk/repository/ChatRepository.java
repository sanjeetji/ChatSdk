package com.sanjeet.chat.sdk.repository;

import com.sanjeet.chat.sdk.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ChatRepository extends JpaRepository<Message,Long> {

    @Query("SELECT m FROM Message m WHERE m.receiverPhone = :receiver_phone AND m.apiKey = :api_key")
    List<Message> findAllByPhoneNumber(@Param("receiver_phone") String receiverPhone, @Param("api_key") String apiKey);

}
