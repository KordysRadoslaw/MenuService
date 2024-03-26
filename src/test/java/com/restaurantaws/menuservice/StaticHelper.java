package com.restaurantaws.menuservice;

import com.restaurantaws.menuservice.services.S3Downloader;

public class StaticHelper {
    public static String downloadJsonFromS3(S3Downloader s3Downloader, String bucketName, String key) {
        return s3Downloader.downloadJsonFromS3(bucketName, key);
    }
}
