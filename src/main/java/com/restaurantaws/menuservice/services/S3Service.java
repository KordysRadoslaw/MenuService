package com.restaurantaws.menuservice.services;

import com.google.gson.Gson;
import com.restaurantaws.menuservice.model.Menu;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

public class S3Service {

    //not use
    public void saveToS3(Menu menu){
        try{
            S3Uploader.uploadToS3(menu);
        }catch (Exception e){
            throw new RuntimeException("Error while saving data to S3: " + e.getMessage());
        }
    }

    //not use
    public Menu loadFromS3(){
        String bucketName = "menudatabase-menu";
        String menuKey = "menu.json";

        try{
            String json = S3Downloader.downloadJsonFromS3(bucketName, menuKey);
            Gson gson = new Gson();
            return gson.fromJson(json, Menu.class);
        }catch (Exception e){
            throw new RuntimeException("Error while loading data from S3. Bucket: " + bucketName + ", Key: " + menuKey + ". " + e.getMessage());
        }
    }
}
