package com.example.app.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "dbsd", name = "S3_BUCKET_DETAILS")
public class S3BucketDetailModel {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name ="BUCKET_NAME")
    private String bucketName;
}
