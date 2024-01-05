package com.example.app.repositories;

import com.example.app.models.S3BucketDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3BucketDetailModelRepository  extends JpaRepository<S3BucketDetailModel,Integer> {

    boolean existsByBucketName(String bucketName);
}
