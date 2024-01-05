package com.example.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class DiscoverEc2InstancesService {

    private Ec2Client ec2Client;

    @Autowired
    private final Environment env;

    public DiscoverEc2InstancesService(Environment env) {
        this.env = env;
        initializeEC2Client();
    }

    void initializeEC2Client(){
        Region region = Region.AP_SOUTH_1;
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(env.getProperty("application.aws.accesskey"),
                env.getProperty("application.aws.secretkey"));
        ec2Client = Ec2Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public List<Instance> getAllEC2Instances() throws Ec2Exception {
        List<Instance> instanceList = new ArrayList<>();
        try {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse response = ec2Client.describeInstances(request);
            for (Reservation reservation : response.reservations()) {
                instanceList.addAll(reservation.instances());
            }
            log.info("EC2 instances found : {}", instanceList);
            return instanceList;
        } catch (Ec2Exception e) {
            log.error("Exception occurred during getAllEC2Instances with error code : {} ", e.awsErrorDetails().errorCode(), e);
            throw e;
        }
    }
}