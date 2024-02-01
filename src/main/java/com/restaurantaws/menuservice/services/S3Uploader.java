package com.restaurantaws.menuservice.services;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public class S3Uploader {


    public static void uploadJsonToS3(String bucketName, String key, String jsonObject ){

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

