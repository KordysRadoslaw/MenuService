package com.restaurantaws.menuservice.services;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;
import com.restaurantaws.menuservice.model.Menu;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3Uploader {

    private final AmazonS3 s3Client;

    public S3Uploader() {
        this.s3Client = AmazonS3ClientBuilder.standard().withCredentials(DefaultAWSCredentialsProviderChain.getInstance()).build();
    }

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

