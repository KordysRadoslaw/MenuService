package com.restaurantaws.menuservice.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * GenerateToken class is used to generate a unique token.
 */
public class GenerateToken {

    public String generateUniqueToken() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] tokenBytes = new byte[32];
            secureRandom.nextBytes(tokenBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating unique token: " + e);
        }
    }
}
