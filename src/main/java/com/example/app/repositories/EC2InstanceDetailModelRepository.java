package com.example.app.repositories;

import com.example.app.models.EC2InstanceDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EC2InstanceDetailModelRepository extends JpaRepository<EC2InstanceDetailModel,Integer> {

    boolean existsByInstanceId(String instanceId);
}
