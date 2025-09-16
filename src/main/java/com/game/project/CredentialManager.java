package com.game.project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CredentialManager {

    private final String secretKey;

    public CredentialManager(@Value("${SECRET_KEY}") String secretKey) {
        this.secretKey = secretKey;
        System.out.println("Secret key is: " + secretKey);
    }

    public String getSecretKey() {
        return secretKey;
    }
}
