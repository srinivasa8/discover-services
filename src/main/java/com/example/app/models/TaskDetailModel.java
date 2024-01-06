package com.example.app.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(schema = "DSDB", name = "TASK_DETAIL")
public class TaskDetailModel {

    @Id
    @Column(name = "JOB_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobId;

    @Column(name = "TASK")
    private String task;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "FAILURE_DETAILS")
    private String failureDetails;

}