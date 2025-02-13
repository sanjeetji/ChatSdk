package com.sanjeet.chat.sdk.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import static com.sanjeet.chat.sdk.utils.Constant.*;

public class SecretKeyGenerator {


    public static void initializeKeys() {
        setSecretKeyMacOs(ADMIN_ENV);
        setSecretKeyMacOs(CLIENT_ENV);
        setSecretKeyMacOs(USER_ENV);
    }

    private static void setSecretKeyMacOs(String envVariable) {
        try {
            // Check if the variable is already set in the system
            String existingValue = System.getenv(envVariable);
            if (existingValue != null && !existingValue.isEmpty()) {
                System.out.println(envVariable + " Exists: " + existingValue);
                return; // Do not overwrite an existing key
            }
            // Generate new key
            String generatedKey = generateKey();
            // Set the environment variable for the current session
            String setCommand = String.format("launchctl setenv %s \"%s\"", envVariable, generatedKey);
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", setCommand});
            // Persist the variable permanently in ~/.zshrc (or ~/.bashrc)
            saveToShellConfig(envVariable, generatedKey);
            System.out.println(envVariable + " Generated and Persisted: " + generatedKey);
        } catch (Exception e) {
            System.err.println("Error setting " + envVariable + " in macOS: " + e.getMessage());
        }
    }

    // Save the key permanently in ~/.zshrc or ~/.bashrc
    private static void saveToShellConfig(String envVariable, String value) {
        String shellConfigPath = System.getProperty("user.home") + "/.zshrc"; // Default for macOS users
        if (!new java.io.File(shellConfigPath).exists()) {
            shellConfigPath = System.getProperty("user.home") + "/.bashrc"; // Fallback for bash users
        }
        try (FileWriter writer = new FileWriter(shellConfigPath, true)) {
            writer.write("\nexport " + envVariable + "=\"" + value + "\"\n");
        } catch (IOException e) {
            System.err.println("Error writing to " + shellConfigPath + ": " + e.getMessage());
        }
    }

    public static String generateKey(){
        SecureRandom random = new SecureRandom();
        byte[] secretKey = new byte[32]; // 256-bit key
        random.nextBytes(secretKey);
        String base64SecretKey = Base64.getEncoder().encodeToString(secretKey);
        System.out.println("Generated Secret Key: " + base64SecretKey);
        return base64SecretKey;
    }

    private static void setSecretKeyTemp(String envVariable) {
        if (System.getenv(envVariable) == null) {  // If key is not already set
            String generatedKey = generateKey();
            System.setProperty(envVariable, generatedKey);  // Temporary setting for runtime
            System.out.println(envVariable + " Generated: " + generatedKey);
        } else {
            System.out.println(envVariable + " Exists: " + System.getenv(envVariable));
        }
    }

    private static void setSecretKeyEvenAfterSystemRebootsForWindowOs(String envVariable) {
        if (System.getenv(envVariable) == null) {
            String generatedKey = generateKey();
            System.setProperty(envVariable, generatedKey);

            // ✅ Persist key permanently (Windows)
            try {
                String command = String.format("setx %s \"%s\" /M", envVariable, generatedKey);
                Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command});
            } catch (Exception e) {
                System.err.println("Error persisting " + envVariable + " in Windows Environment Variables");
            }

            System.out.println(envVariable + " Generated and Persisted: " + generatedKey);
        } else {
            System.out.println(envVariable + " Exists: " + System.getenv(envVariable));
        }
    }

}
