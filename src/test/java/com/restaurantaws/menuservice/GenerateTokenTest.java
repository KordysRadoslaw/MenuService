package com.restaurantaws.menuservice;

import com.restaurantaws.menuservice.services.GenerateToken;
import org.junit.Assert;
import org.junit.Test;

public class GenerateTokenTest {

    @Test
    public void testGenerateUniqueToken() {
        // Arrange
        GenerateToken generateToken = new GenerateToken();
        // Act
        String token =  generateToken.generateUniqueToken();
        // Assert
        Assert.assertNotNull(token);
    }
}
