package com.example.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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

    @Column(name ="CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
}
