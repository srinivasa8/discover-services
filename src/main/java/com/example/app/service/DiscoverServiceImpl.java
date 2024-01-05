package com.example.app.service;

import com.example.app.common.JobStatus;
import com.example.app.models.EC2InstanceDetailModel;
import com.example.app.models.S3BucketDetailModel;
import com.example.app.models.S3BucketFileDetailModel;
import com.example.app.models.TaskDetailModel;
import com.example.app.repositories.EC2InstanceDetailModelRepository;
import com.example.app.repositories.S3BucketDetailModelRepository;
import com.example.app.repositories.S3BucketFileDetailModelRepository;
import com.example.app.repositories.TaskDetailModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiscoverServiceImpl implements DiscoverService{
    @Autowired
    private S3BucketFileDetailModelRepository s3BucketFileDetailModelRepository;

    @Autowired
    DiscoverEc2InstancesService discoverEc2InstancesService;

    @Autowired
    DiscoverS3BucketsService discoverS3BucketsService;

    @Autowired
    EC2InstanceDetailModelRepository ec2InstanceDetailModelRepository;

    @Autowired
    S3BucketDetailModelRepository s3BucketDetailModelRepository;

    @Autowired
    TaskDetailModelRepository taskDetailModelRepository;

    @Autowired
    Executor executor;

    @Override
    public int discoverServices(List<String> services) {
        try {
            int jobId = generateNewTaskRecord("DiscoverServices");
            for (String service : services) {
                if (service.equalsIgnoreCase("EC2")) {
                    CompletableFuture.runAsync(() -> processEC2Discovery(jobId), executor);
                }
                if (service.equalsIgnoreCase("S3")) {
                    CompletableFuture.runAsync(() -> processS3BucketDiscovery(jobId), executor);
                }
                log.info("Task running in background with id :" + jobId);
            }
            return jobId;
        } catch (Exception e) {
            log.info("Task happened during discoverServices for the services : {} ", services, e);
        }
        return 0;
    }

    @Override
    public String getJobResult(int jobId) {
        String status = "No job found";
        try {
            Optional<TaskDetailModel> taskDetailModelOptional = taskDetailModelRepository.findById(jobId);
            if (taskDetailModelOptional.isPresent()) {
                status = taskDetailModelOptional.get().getStatus();
            }
            log.info("Status for job with id : {} is : {}", jobId, status);
        } catch (Exception e) {
            status= "Error happened while finding job result for jobId :"+ jobId;
            log.error("Exception happened while getting job result for jobId : {} ", jobId, e);
        }
        return status;
    }

    @Override
    public List<String> getDiscoveryResult(String service) {
        log.info("Started getDiscoveryResult for service : {}", service);
        List<String> resultList = new ArrayList<>();
        try {
            if (service.equalsIgnoreCase("EC2")) {
                resultList = getAllEC2InstanceIds();
            } else if (service.equalsIgnoreCase("S3")){
                resultList = getAllS3BucketNames();
            }
        } catch (Exception e) {
            log.error("Exception happened during getDiscoveryResult for service : {} ", service);
        }
        log.info("Results fetched for {} are : {} ", service, resultList);
        return resultList;
    }

    private List<String> getAllEC2InstanceIds(){
        List<String> instnceIdList= new ArrayList<>();
        List<EC2InstanceDetailModel> ec2InstanceDetailModelList = ec2InstanceDetailModelRepository.findAll();
        instnceIdList = ec2InstanceDetailModelList.stream().map(i->i.getInstanceId()).collect(Collectors.toList());
        return instnceIdList;
    }

    private List<String> getAllS3BucketNames(){
        List<String> bucketList= new ArrayList<>();
        List<S3BucketDetailModel> s3BucketDetailModellList = s3BucketDetailModelRepository.findAll();
        bucketList = s3BucketDetailModellList.stream().map(b->b.getBucketName()).collect(Collectors.toList());
        return bucketList;
    }

    @Override
    public int getS3BucketObjects(String bucketName) {
        log.info("Started getS3BucketObjects for bucketName : {}", bucketName);
        try {
            int jobId = generateNewTaskRecord("getS3BucketObjects");
            CompletableFuture.runAsync(() -> processGetS3Objects(jobId, bucketName), executor);
            log.info("Task running in background with id : {}", jobId);
            return jobId;
        } catch (Exception e) {
            log.error("Task happened during getS3BucketObjects for the bucket : {} ", bucketName, e);
        }
        return 0;
    }

    @Override
    public int getS3BucketObjectCount(String bucketName) {
        return 0;
    }

    @Override
    public List<String> getS3BucketObjectlike(String bucketName, String pattern) {
        return null;
    }

    private void processEC2Discovery(int jobId){
        String status = JobStatus.Failed.toString();
        String failureDetails = null;
        try {
            Thread.sleep(3000);
            List<Instance> instanceList = discoverEc2InstancesService.getAllEC2Instances();
            saveInstances(instanceList);
            status = JobStatus.Success.toString();
            log.info("Task completed in background with id : {}", jobId);
        } catch (Exception e){
            log.error("Exception occurred while EC2 discovery for job id : {} ", jobId, e);
            failureDetails = "EC2 discovery failed with error: " + e.getMessage();
        } finally {
            saveTaskDetail(jobId, status, failureDetails);
        }
    }

    private void saveInstances(List<Instance> instanceList){
        for(Instance instance : instanceList){
            boolean isInstanceDetailExist  = ec2InstanceDetailModelRepository.existsByInstanceId(instance.instanceId());
            if(!isInstanceDetailExist) {
                EC2InstanceDetailModel instanceModel = new EC2InstanceDetailModel();
                instanceModel.setInstanceId(instance.instanceId());
                instanceModel.setImageId(instance.imageId());
                instanceModel.setType(instance.instanceType().toString());
                instanceModel.setState(instance.state().name().toString());
                ec2InstanceDetailModelRepository.save(instanceModel);
                log.info("Inserted a record for EC2 instance with instanceId : {} ", instance.instanceId());
            } else{
                log.info("EC2 instance with instanceId : {}, is already present in DB, So skipping inserting it!", instance.instanceId());
            }
        }
    }

    public void processS3BucketDiscovery(int jobId){
        String status = JobStatus.Failed.toString();
        String failureDetails = null;
        try {
            Thread.sleep(3000);
            List<Bucket> bucketList = discoverS3BucketsService.getAllS3Buckets();
            saveS3BucketDetails(bucketList);
            status = JobStatus.Success.toString();
            log.info("ProcessS3BucketDiscovery task completed in background with job id : {} ", jobId);
        } catch (Exception e){
            log.error("Exception occurred while S3 discovery for job id : {} ", jobId, e);
            failureDetails = "S3 Discovery failed with error: " + e.getMessage();
        } finally {
            saveTaskDetail(jobId, status, failureDetails);
        }
    }

    public void processGetS3Objects(int jobId, String bucketName){
        String status = JobStatus.Failed.toString();
        String failureDetails = null;
        try {
            Thread.sleep(3000);
            List<S3Object> s3ObjectList = discoverS3BucketsService.getS3BucketObjectsByBucket(bucketName);
            saveS3BucketObjectDetails(s3ObjectList, bucketName);
            status = JobStatus.Success.toString();
            log.info("processGetS3Objects task completed in background with job id : {} ", jobId);
        } catch (Exception e){
            log.error("Exception occurred while processGetS3Objects for job id : {} ", jobId, e);
            failureDetails = "processGetS3Objects failed with error: " + e.getMessage();
        } finally {
            saveTaskDetail(jobId, status, failureDetails);
        }
    }

    private void saveS3BucketObjectDetails(List<S3Object> s3ObjectList, String bucketName) {
        for(S3Object s3Object : s3ObjectList){
            //s3Object.lastModified();
           // boolean isBucketDetailExist = s3BucketDetailModelRepository.existsByBucketName(bucket.name());
            //if(!isBucketDetailExist){
                S3BucketFileDetailModel  s3BucketFileDetailModel = new S3BucketFileDetailModel();
                s3BucketFileDetailModel.setBucketName(bucketName);
               // log.info("--->"+s3Object.key());
                String[] fileData = s3Object.key().split("/");
                s3BucketFileDetailModel.setFileName(fileData[1]);
                s3BucketFileDetailModel.setFolderName(fileData[0]);
                s3BucketFileDetailModel.setSize(s3Object.size());
                s3BucketFileDetailModel.setOwnerId(s3Object.owner().id());
                s3BucketFileDetailModel.setLastModifiedDate(Date.from(s3Object.lastModified()));
               // s3BucketFileDetailModelRepository.exists(s3BucketFileDetailModel);
                s3BucketFileDetailModelRepository.save(s3BucketFileDetailModel);
                log.info("Inserted a record for s3 file with name : {} ", fileData[1]);
//            } else{
//                log.info("S3 bucket already exist with name : {}, is already present in DB, So skipping inserting it!", bucket.name());
//            }
        }

    }

    private void saveS3BucketDetails(List<Bucket> bucketList) {
        for(Bucket bucket : bucketList){
            boolean isBucketDetailExist = s3BucketDetailModelRepository.existsByBucketName(bucket.name());
            if(!isBucketDetailExist){
                S3BucketDetailModel  s3BucketDetailModel = new S3BucketDetailModel();
                s3BucketDetailModel.setBucketName(bucket.name());
                s3BucketDetailModelRepository.save(s3BucketDetailModel);
                log.info("Inserted a record for s3 bucket with name : {} ", bucket.name());
            } else{
                log.info("S3 bucket already exist with name : {}, is already present in DB, So skipping inserting it!", bucket.name());
            }
        }

    }

    private void saveTaskDetail(int jobId, String status, String failureDetails) {
        Optional<TaskDetailModel> taskModelOptional = taskDetailModelRepository.findById(jobId);
        if(taskModelOptional.isPresent()){
            TaskDetailModel taskModel = taskModelOptional.get();
            taskModel.setStatus(status);
            String updatedFailureDetails = StringUtils.hasLength(taskModel.getFailureDetails()) ?
                    (taskModel.getFailureDetails() + " and " + failureDetails) : taskModel.getFailureDetails();
            taskModel.setFailureDetails(updatedFailureDetails);
            taskDetailModelRepository.save(taskModel);
        } else{
            log.info("No Job found with given jobId: {}", jobId);
        }
    }

    private int generateNewTaskRecord(String task){
        TaskDetailModel taskModel = new TaskDetailModel();
        taskModel.setTask(task);
        taskModel.setStatus(JobStatus.inProgress.toString());
        taskDetailModelRepository.save(taskModel);
        System.out.println("id:"+taskModel.getJobId());
        return taskModel.getJobId();
    }
}
