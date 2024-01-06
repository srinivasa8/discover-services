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
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(schema = "dbsd", name = "EC2_INSTANCE_DETAIL")
public class EC2InstanceDetailModel {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name ="INSTANCE_ID")
    private String instanceId;

    @Column(name ="IMAGE_ID")
    private String imageId;

    @Column(name ="TYPE")
    private String type;

    @Column(name ="STATE")
    private String state;

    @Column(name ="MONITORING_STATE")
    private String monitoringState;

    @Column(name ="LAUNCH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date launchDate;
}
