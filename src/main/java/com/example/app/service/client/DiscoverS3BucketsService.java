package com.example.app.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DiscoverS3BucketsService {

    @Autowired
    private final Environment env;

    private S3Client s3Client;

    public DiscoverS3BucketsService(Environment env) {
        this.env = env;
        initializeS3Client();
    }

    void initializeS3Client(){
        Region region = Region.of(env.getProperty("application.aws.region"));
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(env.getProperty("application.aws.accesskey"),
                env.getProperty("application.aws.secretkey"));
        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public List<Bucket> getAllS3Buckets() throws S3Exception {
        List<Bucket> bucketList = new ArrayList<>();
        try {
            ListBucketsResponse response = s3Client.listBuckets();
            bucketList = response.buckets();
            return bucketList;
        } catch (S3Exception e) {
            log.error("Exception occurred during etAllS3Buckets with error code : {} ", e.awsErrorDetails().errorCode(), e);
            throw e;
        }
    }

    public List<S3Object> getS3BucketObjectsByBucket(String bucketName) throws S3Exception {
        List<S3Object> s3ObjectList = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsResponse res = s3Client.listObjects(listObjects);
            s3ObjectList = res.contents();
            return s3ObjectList;
        } catch (S3Exception e) {
            log.error("Exception occurred during getS3BucketObjectsByBucket with error code : {} ", e.awsErrorDetails().errorCode(), e);
            throw e;
        }
    }
}