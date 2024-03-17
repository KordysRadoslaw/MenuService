package com.restaurantaws.menuservice.services;

import com.google.gson.Gson;
import com.restaurantaws.menuservice.model.Menu;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3Uploader {

    private static final Gson gson = new Gson();

    public static void uploadToS3(Menu menu){
        String bucketName = "menudatabase-menu";
        String key = String.format("menu%.1f.json", menu.getMenuVersion());
        System.out.println("menuV: " + menu.getMenuVersion());
        String jsonObject = gson.toJson(menu);

        S3Client s3Client = S3ClientFactory.createS3Client();

        PutObjectResponse response = s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                RequestBody.fromString(jsonObject)
        );
        System.out.println("Uploaded to S3: " + response);
    }



}

