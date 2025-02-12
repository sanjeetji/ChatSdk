package com.sanjeet.chat.sdk.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {

    public static String generateKey(){
        SecureRandom random = new SecureRandom();
        byte[] secretKey = new byte[32]; // 256-bit key
        random.nextBytes(secretKey);
        String base64SecretKey = Base64.getEncoder().encodeToString(secretKey);
        System.out.println("Generated Secret Key: " + base64SecretKey);
        return base64SecretKey;
    }
}
