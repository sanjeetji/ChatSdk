package com.sanjeet.chat.sdk.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import static com.sanjeet.chat.sdk.utils.Constant.*;

public class SecretKeyGenerator {


    public static void initializeKeys() {
        if (System.getenv(ENV_VAR) == null) {
            String key = generateKey();
            saveToShellConfig(ENV_VAR, key);
            setKeyForCurrentSession(ENV_VAR, key);
            System.out.println("✅ JWT_SECRET generated and stored: " + key);
        } else {
            System.out.println("✅ JWT_SECRET already set: " + getSecret());
        }
    }

    public static String getSecret() {
        return System.getenv(ENV_VAR);
    }

    public static String generateKey() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static void saveToShellConfig(String key, String value) {
        String config = System.getProperty("user.home") + "/.zshrc";
        try (FileWriter writer = new FileWriter(config, true)) {
            writer.write("\nexport " + key + "=\"" + value + "\"\n");
        } catch (IOException e) {
            System.err.println("❌ Failed to write to shell config: " + e.getMessage());
        }
    }

    private static void setKeyForCurrentSession(String key, String value) {
        try {
            String cmd = String.format("launchctl setenv %s \"%s\"", key, value);
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
        } catch (IOException e) {
            System.err.println("❌ Failed to set key in session: " + e.getMessage());
        }
    }

}
