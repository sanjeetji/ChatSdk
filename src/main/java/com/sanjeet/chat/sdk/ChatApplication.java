package com.sanjeet.chat.sdk;

import com.sanjeet.chat.sdk.utils.SecretKeyGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.sanjeet.chat.sdk.utils.Constant.ENV_VAR;

@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);
		// Debugging at startup
		System.out.println("🔍 Server Started - Checking Environment Variables...");
		System.out.println("🔍 All Available Environment Variables: " + System.getenv());
		System.out.println("🔍 JWT_SECRET KEY IS : " + System.getenv(ENV_VAR));
	}

	@PostConstruct
	public void init() {
		// ✅ Initialize Secret Keys on Application Startup
		SecretKeyGenerator.initializeKeys();
	}

}
