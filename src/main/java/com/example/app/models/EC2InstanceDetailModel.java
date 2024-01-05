package com.example.app.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
//@Builder
@RequiredArgsConstructor
//@NoArgsConstructor
@Table(schema = "dbsd", name = "EC2_INSTANCE_DETAILS")
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

}
