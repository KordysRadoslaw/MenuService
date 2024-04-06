package com.restaurantaws.menuservice.services;


/**
 * Interface for downloading JSON from S3
 */
public interface S3DownloadInterface {

    /**
     * Download JSON from S3
     * @param bucketName
     * @param key
     * @return JSON string
     */
    String downloadJsonFromS3(String bucketName, String key);
}
