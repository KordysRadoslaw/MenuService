package com.restaurantaws.menuservice.services;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Factory class to create an S3 client

 */
public class S3ClientFactory {
    public static S3Client createS3Client() {
        return S3Client.builder()
                .region(Region.EU_WEST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }
}