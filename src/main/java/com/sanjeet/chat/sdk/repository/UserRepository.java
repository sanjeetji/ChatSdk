package com.sanjeet.chat.sdk.repository;

import com.sanjeet.chat.sdk.model.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<ClientUser, Long> {

}
