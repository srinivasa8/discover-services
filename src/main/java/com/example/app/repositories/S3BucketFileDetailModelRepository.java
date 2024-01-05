package com.example.app.repositories;

import com.example.app.models.S3BucketFileDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3BucketFileDetailModelRepository  extends JpaRepository<S3BucketFileDetailModel,Integer> {
}
