package com.example.app.service;

import java.util.List;

public interface DiscoverService {

    int discoverServices(List<String> services) throws Exception;

    String getJobResult(int jobId) throws Exception;

    List<String> getDiscoveryResult(String service) throws Exception;

    int getS3BucketObjects(String bucketName) throws Exception;

    int getS3BucketObjectCount(String bucketName) throws Exception;

    List<String> getS3BucketObjectLike(String bucketName, String pattern) throws Exception;
}
