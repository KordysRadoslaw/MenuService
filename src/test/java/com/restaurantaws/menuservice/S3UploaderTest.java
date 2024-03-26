package com.restaurantaws.menuservice;

import com.google.gson.Gson;
import com.restaurantaws.menuservice.model.Category;
import com.restaurantaws.menuservice.model.Dish;
import com.restaurantaws.menuservice.model.Drink;
import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.services.S3Uploader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class S3UploaderTest {

    @Mock
    private S3Client mockS3Client;

    @Mock
    private PutObjectResponse mockPutObjectResponse;

    @Before
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }
//tutaj jakis glupi blad
    @Test
    public void testUploadToS3_ValidMenu_CallsS3ClientCorrectly() {
        // Arrange
        Menu menu = new Menu(1.2f, "2023-11-21", "abc123",
                Arrays.asList(new Dish("Pasta", "Danie obiadowe", "12.5f", "Main", "Makaron z sosem pomidorowym")),
                Collections.singletonList(new Drink("Drink", "Cola", "Cola", "3.5f", "Soft")),
                Collections.emptyList(), Category.MAIN_DISH, 1669054400L);
        String expectedBucketName = "menudatabase-menu";
        String expectedKey = String.format("menu%.1f.json", menu.getMenuVersion());
        String expectedJson = new Gson().toJson(menu);

        when(mockS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(mockPutObjectResponse);

        // Act
        S3Uploader.uploadToS3(menu);

        // Assert
        verify(mockS3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
