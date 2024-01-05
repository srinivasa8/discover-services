package com.example.app.service;

import java.util.List;

public interface DiscoverService {

    int discoverServices(List<String> services);

    String getJobResult(int jobId);

    List<String> getDiscoveryResult(String service);

    int getS3BucketObjects(String bucketName);

    int getS3BucketObjectCount(String bucketName);

    List<String> getS3BucketObjectlike(String bucketName, String pattern);
}
