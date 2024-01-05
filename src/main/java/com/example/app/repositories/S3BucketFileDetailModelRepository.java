package com.example.app.repositories;

import com.example.app.models.S3BucketFileDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface S3BucketFileDetailModelRepository  extends JpaRepository<S3BucketFileDetailModel,Integer> {

    @Query("select s from S3BucketFileDetailModel s where s.bucketName = ?1 and s.folderName = ?2 and s.fileName = ?3")
    Optional<S3BucketFileDetailModel> findByBucketNameAndFolderNameAndFileName(String bucketName, String folderName, String fileName);

    @Query("select count(s) from S3BucketFileDetailModel s where s.bucketName = ?1")
    int countByBucketName(String bucketName);

    List<S3BucketFileDetailModel> findByBucketNameAndFileNameLike(String bucketName, String fileName);

}
