package com.example.app.repositories;

import com.example.app.models.EC2InstanceDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EC2InstanceDetailModelRepository extends JpaRepository<EC2InstanceDetailModel,Integer> {

    Optional<EC2InstanceDetailModel> findByInstanceId(String instanceId);

}
