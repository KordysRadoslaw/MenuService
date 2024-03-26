package com.restaurantaws.menuservice;

import com.google.gson.Gson;
import com.restaurantaws.menuservice.model.Menu;
import com.restaurantaws.menuservice.services.S3Downloader;
import com.restaurantaws.menuservice.services.S3Service;
import com.restaurantaws.menuservice.services.S3Uploader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class S3ServiceTest {

    @Test
    public void testSaveToS3Successful() {
        // Arrange
        Menu menu = mock(Menu.class);
        S3Uploader s3Uploader = mock(S3Uploader.class);
        S3Downloader s3Downloader = mock(S3Downloader.class);
        S3Service s3Service = new S3Service(s3Uploader, s3Downloader);

        // Act
        s3Service.saveToS3(menu);

        // Assert
        verify(s3Uploader).uploadToS3(menu);
    }

    @Test
    public void testLoadFromS3Successful() {
        // Arrange
        String json = "{\"menuVersion\":1.0,\"date\":\"2023-11-21\",\"token\":\"abc123\",\"dishes\":[{\"name\":\"Pasta\",\"description\":\"Danie obiadowe\",\"price\":\"12.5f\",\"category\":\"Main\",\"details\":\"Makaron z sosem pomidorowym\"}],\"drinks\":[{\"name\":\"Drink\",\"brand\":\"Cola\",\"type\":\"Soft\",\"price\":\"3.5f\",\"category\":\"Cola\"}],\"addsOn\":[],\"category\":\"MAIN_DISH\",\"timestamp\":1669054400}";
        S3Downloader s3Downloader = mock(S3Downloader.class);
        S3Uploader s3Uploader = mock(S3Uploader.class);
        Gson gson = new Gson();
        Menu expectedMenu = gson.fromJson(json, Menu.class);


        // StaticHelper its helpoer class with static methods
        when(StaticHelper.downloadJsonFromS3(s3Downloader, "menudatabase-menu", "menu.json")).thenReturn(json);


        S3Service s3Service = new S3Service(s3Uploader, s3Downloader);

        // Act
        Menu loadedMenu = s3Service.loadFromS3();

        // Assert
        assertEquals(expectedMenu, loadedMenu);
    }
}

