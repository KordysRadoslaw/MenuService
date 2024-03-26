package com.restaurantaws.menuservice.services;

public interface S3DownloadInterface {
    String downloadJsonFromS3(String bucketName, String key);
}
