package com.example.app.service;

import com.example.app.common.JobStatus;
import com.example.app.exception.InvalidInputException;
import com.example.app.models.EC2InstanceDetailModel;
import com.example.app.models.S3BucketDetailModel;
import com.example.app.models.S3BucketFileDetailModel;
import com.example.app.models.TaskDetailModel;
import com.example.app.repositories.EC2InstanceDetailModelRepository;
import com.example.app.repositories.S3BucketDetailModelRepository;
import com.example.app.repositories.S3BucketFileDetailModelRepository;
import com.example.app.repositories.TaskDetailModelRepository;
import com.example.app.service.client.DiscoverEc2InstancesService;
import com.example.app.service.client.DiscoverS3BucketsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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

import static com.example.app.common.Constants.ACCEPTABLE_SERVICE_LIST;

@Service
@Slf4j
public class DiscoverServiceImpl implements DiscoverService{
    @Autowired
    private S3BucketFileDetailModelRepository s3BucketFileDetailModelRepository;

    @Autowired
    private DiscoverEc2InstancesService discoverEc2InstancesService;

    @Autowired
    private DiscoverS3BucketsService discoverS3BucketsService;

    @Autowired
    private EC2InstanceDetailModelRepository ec2InstanceDetailModelRepository;

    @Autowired
    private S3BucketDetailModelRepository s3BucketDetailModelRepository;

    @Autowired
    private TaskDetailModelRepository taskDetailModelRepository;

    @Autowired
    private Executor executor;

    @Override
    public int discoverServices(List<String> services) throws Exception {
        log.info("Started discoverServices for services : {} ", services);
        if(CollectionUtils.isEmpty(services)){
            throw new InvalidInputException("Input is empty! Please provide both or any one of the following : " + ACCEPTABLE_SERVICE_LIST);
        }
        boolean isInputInvalid = services.stream().anyMatch(s-> !ACCEPTABLE_SERVICE_LIST.contains(s.toUpperCase()));
        if(isInputInvalid){
            throw new InvalidInputException("Input contains invalid value! Please use both or any one of the following : " + ACCEPTABLE_SERVICE_LIST);
        }
        //Removing duplicate values to avoid processing the same service multiple times
        List<String> serviceList = services.stream().map(s -> s.toUpperCase()).distinct().toList();
        log.info("Services after filtering duplicates : {} ", serviceList);
        try {
            int jobId = generateNewTaskRecord("DiscoverServices");
            for (String service : serviceList) {
                if (service.equalsIgnoreCase("EC2")) {
                    CompletableFuture.runAsync(() -> processEC2Discovery(jobId), executor);
                }
                if (service.equalsIgnoreCase("S3")) {
                    CompletableFuture.runAsync(() -> processS3BucketDiscovery(jobId), executor);
                }
            }
            log.info("Task scheduled and running in the background with jobId :" + jobId);
            return jobId;
        } catch (Exception e) {
            log.info("An error happened during discoverServices for the services : {} ", services, e);
            throw new Exception("An error occurred while discovering services : " + services + ", Please try again after sometime!");
        }
    }

    @Override
    public String getJobResult(int jobId) throws Exception {
        log.info("Started getJobResult for jobId : {} ", jobId);
        if (jobId <= 0) {
            throw new InvalidInputException("Input jobId is invalid!");
        }
        String status = null;
        try {
            Optional<TaskDetailModel> taskDetailModelOptional = taskDetailModelRepository.findById(jobId);
            if (taskDetailModelOptional.isPresent()) {
                status = taskDetailModelOptional.get().getStatus();
                log.info("Result found for jobId : {} is : {}", jobId, status);
            } else{
                status = "No Job found with the requested jobId : " + jobId;
                log.info("No Job found with the requested jobId : {}", jobId);
            }
            return status;
        } catch (Exception e) {
            log.error("Exception happened while getting job result for jobId : {} ", jobId, e);
            throw new Exception("An Error happened while finding the job result for jobId : " + jobId + ", please try again after sometime!");
        }
    }

    @Override
    public List<String> getDiscoveryResult(String service) throws Exception {
        log.info("Started getDiscoveryResult for service : {}", service);
        if (!StringUtils.hasLength(service)) {
            throw new InvalidInputException("Input is empty! Please provide any one of the following : " + ACCEPTABLE_SERVICE_LIST);
        }
        if (!ACCEPTABLE_SERVICE_LIST.contains(service.toUpperCase())) {
            throw new InvalidInputException("Input contains invalid value! Please provide any one of the following : " + ACCEPTABLE_SERVICE_LIST);
        }
        List<String> resultList = new ArrayList<>();
        try {
            if (service.equalsIgnoreCase("EC2")) {
                resultList = getAllEC2InstanceIds();
            } else if (service.equalsIgnoreCase("S3")) {
                resultList = getAllS3BucketNames();
            }
            log.info("Results fetched for {} are : {} ", service, resultList);
            return resultList;
        } catch (Exception e) {
            log.error("Exception happened during getDiscoveryResult for service : {} ", service, e);
            throw new Exception("An error occurred while getting discovery result for service : " + service + ", Please try again after sometime!");
        }
    }

    private List<String> getAllEC2InstanceIds(){
        List<String> instnceIdList= new ArrayList<>();
        List<EC2InstanceDetailModel> ec2InstanceDetailModelList = ec2InstanceDetailModelRepository.findAll();
        instnceIdList = ec2InstanceDetailModelList.stream().map(i -> i.getInstanceId()).collect(Collectors.toList());
        return instnceIdList;
    }

    private List<String> getAllS3BucketNames(){
        List<String> bucketList= new ArrayList<>();
        List<S3BucketDetailModel> s3BucketDetailModellList = s3BucketDetailModelRepository.findAll();
        bucketList = s3BucketDetailModellList.stream().map(b -> b.getBucketName()).collect(Collectors.toList());
        return bucketList;
    }

    @Override
    public int getS3BucketObjects(String bucketName) throws Exception {
        log.info("Started getS3BucketObjects for bucketName : {}", bucketName);
        if (!StringUtils.hasLength(bucketName)) {
            throw new InvalidInputException("Given bucketName is empty! Please provide an valid bucket name.");
        }
        try {
            int jobId = generateNewTaskRecord("getS3BucketObjects");
            CompletableFuture.runAsync(() -> processGetS3Objects(jobId, bucketName), executor);
            log.info("Task running in background with id : {}", jobId);
            return jobId;
        } catch (Exception e) {
            log.error("Exception happened during getS3BucketObjects for the bucketName : {} ", bucketName, e);
            throw new Exception("An error occurred while scheduling job for discovering s3 objects for bucketName : "
                    + bucketName + ", Please try again after sometime!");
        }
    }

    @Override
    public int getS3BucketObjectCount(String bucketName) throws Exception {
        log.info("Started getS3BucketObjectCount for the bucketName : {} ", bucketName);
        if (!StringUtils.hasLength(bucketName)) {
            throw new InvalidInputException("Given bucketName is empty! Please provide an valid bucket name.");
        }
        try {
            return s3BucketFileDetailModelRepository.countByBucketName(bucketName);
        } catch (Exception e) {
            log.error("Exception happened during getS3BucketObjects for the bucketName : {} ", bucketName, e);
            throw new Exception("An error occurred while getting object count for bucketName : "
                    + bucketName + ", Please try again after sometime!");
        }
    }

    @Override
    public List<String> getS3BucketObjectlike(String bucketName, String pattern) throws Exception {
        log.info("Started getS3BucketObjectCount for the bucketName : {} ", bucketName);
        if (!StringUtils.hasLength(bucketName) || !StringUtils.hasLength(pattern)) {
            throw new InvalidInputException("Given bucketName or pattern is empty! Please provide an valid bucket name and pattern.");
        }
        List<String> fileNameList = new ArrayList<>();
        try {
            List<S3BucketFileDetailModel> s3BucketFileDetailModelList = s3BucketFileDetailModelRepository.findByBucketNameAndFileNameLike(bucketName, pattern);
            fileNameList = s3BucketFileDetailModelList.stream().map(s -> s.getFileName()).collect(Collectors.toList());
            log.info("Files found for bucketName : {} and matching pattern : {} are : {}", bucketName, pattern, fileNameList);
            return fileNameList;
        } catch (Exception e) {
            log.error("Exception happened during getS3BucketObjects for the bucketName : {} ", bucketName, e);
            throw new Exception("An error occurred while getting objects for bucketName : "
                    + bucketName + " with pattern : " + pattern + ", Please try again after sometime!");
        }
    }

    private void processEC2Discovery(int jobId){
        log.info("Started processEC2Discovery task in background for jobId : {}", jobId);
        String status = JobStatus.Failed.toString();
        String failureDetails = null;
        try {
            List<Instance> instanceList = discoverEc2InstancesService.getAllEC2Instances();
            saveInstances(instanceList);
            status = JobStatus.Success.toString();
            log.info("processEC2Discovery task completed in background for jobId : {}", jobId);
        } catch (Exception e){
            log.error("Exception occurred while EC2 discovery for job id : {} ", jobId, e);
            failureDetails = "EC2 discovery failed with error: " + e.getMessage();
        } finally {
            saveTaskDetail(jobId, status, failureDetails);
        }
    }

    private void saveInstances(List<Instance> instanceList){
        for(Instance instance : instanceList){
            Optional<EC2InstanceDetailModel> instanceModelOptional  = ec2InstanceDetailModelRepository.findByInstanceId(instance.instanceId());
            if(!instanceModelOptional.isPresent()) {
                EC2InstanceDetailModel instanceModel = new EC2InstanceDetailModel();
                instanceModel.setInstanceId(instance.instanceId());
                instanceModel.setImageId(instance.imageId());
                instanceModel.setType(instance.instanceType().toString());
                instanceModel.setState(instance.state().name().toString());
                instanceModel.setMonitoringState(instance.monitoring().state().toString());
                instanceModel.setLaunchDate(Date.from(instance.launchTime()));
                ec2InstanceDetailModelRepository.save(instanceModel);
                log.info("Inserted a record for EC2 instance with instanceId : {} ", instance.instanceId());
            } else{
                EC2InstanceDetailModel instanceModel = instanceModelOptional.get();
                instanceModel.setImageId(instance.imageId());
                instanceModel.setType(instance.instanceType().toString());
                instanceModel.setState(instance.state().name().toString());
                instanceModel.setMonitoringState(instance.monitoring().state().toString());
                instanceModel.setLaunchDate(Date.from(instance.launchTime()));
                ec2InstanceDetailModelRepository.save(instanceModel);
                log.info("Updating EC2 instance with instanceId : {}, as it is already present in DB!", instance.instanceId());
            }
        }
    }

    private void processS3BucketDiscovery(int jobId){
        log.info("Started ProcessS3BucketDiscovery task in background for jobId : {}", jobId);
        String status = JobStatus.Failed.toString();
        String failureDetails = null;
        try {
            List<Bucket> bucketList = discoverS3BucketsService.getAllS3Buckets();
            saveS3BucketDetails(bucketList);
            status = JobStatus.Success.toString();
            log.info("ProcessS3BucketDiscovery task completed in background for job id : {} ", jobId);
        } catch (Exception e){
            log.error("Exception occurred while S3 discovery for job id : {} ", jobId, e);
            failureDetails = "S3 Discovery failed with error: " + e.getMessage();
        } finally {
            saveTaskDetail(jobId, status, failureDetails);
        }
    }

    private void processGetS3Objects(int jobId, String bucketName){
        String status = JobStatus.Failed.toString();
        String failureDetails = null;
        try {
            List<S3Object> s3ObjectList = discoverS3BucketsService.getS3BucketObjectsByBucket(bucketName);
            saveS3BucketObjectDetails(s3ObjectList, bucketName);
            status = JobStatus.Success.toString();
            log.info("processGetS3Objects task completed in background with job id : {} ", jobId);
        } catch (Exception e){
            log.error("Exception occurred while processGetS3Objects for job id : {} ", jobId, e);
            log.error("-->{}",e.getMessage());
            failureDetails = "processGetS3Objects failed with error: " + e.getMessage();
        } finally {
            saveTaskDetail(jobId, status, failureDetails);
        }
    }

    private void saveS3BucketObjectDetails(List<S3Object> s3ObjectList, String bucketName) {
        if(!CollectionUtils.isEmpty(s3ObjectList)){
            for(S3Object s3Object : s3ObjectList){
                String[] fileData = s3Object.key().split("/");
                // Checking for duplicate record based on bucketName, folderName and fileName
                Optional<S3BucketFileDetailModel> s3BucketFileDetailModelOptional = s3BucketFileDetailModelRepository.findByBucketNameAndFolderNameAndFileName(bucketName, fileData[0],fileData[1]);
                if(!s3BucketFileDetailModelOptional.isPresent()){
                    S3BucketFileDetailModel  s3BucketFileDetailModel = new S3BucketFileDetailModel();
                    s3BucketFileDetailModel.setBucketName(bucketName);
                    s3BucketFileDetailModel.setFileName(fileData[1]);
                    s3BucketFileDetailModel.setFolderName(fileData[0]);
                    s3BucketFileDetailModel.setSize(s3Object.size());
                    s3BucketFileDetailModel.setOwnerId(s3Object.owner().id());
                    s3BucketFileDetailModel.setLastModifiedDate(Date.from(s3Object.lastModified()));
                    s3BucketFileDetailModelRepository.save(s3BucketFileDetailModel);
                    log.info("Inserted a record for s3 file with name : {} for bucketName : {} ", fileData[1], bucketName);
                } else{
                    // Updating size, ownerId, lastModifiedDate as these data may change over the time.
                    S3BucketFileDetailModel  s3BucketFileDetailModel = s3BucketFileDetailModelOptional.get();
                    s3BucketFileDetailModel.setSize(s3Object.size());
                    s3BucketFileDetailModel.setOwnerId(s3Object.owner().id());
                    s3BucketFileDetailModel.setLastModifiedDate(Date.from(s3Object.lastModified()));
                    s3BucketFileDetailModelRepository.save(s3BucketFileDetailModel);
                    log.info("Updated a record for s3 file with name : {} for bucketName : {}", fileData[1], bucketName);
                }
            }
        } else{
            log.info("No files found for bucketName: {}", bucketName);
        }
    }

    private void saveS3BucketDetails(List<Bucket> bucketList) {
        for(Bucket bucket : bucketList){
            boolean isBucketDetailExist = s3BucketDetailModelRepository.existsByBucketName(bucket.name());
            if(!isBucketDetailExist){
                S3BucketDetailModel  s3BucketDetailModel = new S3BucketDetailModel();
                s3BucketDetailModel.setBucketName(bucket.name());
                s3BucketDetailModel.setCreatedDate(Date.from(bucket.creationDate()));
                s3BucketDetailModelRepository.save(s3BucketDetailModel);
                log.info("Inserted a record for s3 bucket with name : {} ", bucket.name());
            } else{
                log.info("S3 bucket ith name : {}, is already present in DB, So skipping inserting it!", bucket.name());
            }
        }

    }

    private void saveTaskDetail(int jobId, String status, String failureDetails) {
        Optional<TaskDetailModel> taskModelOptional = taskDetailModelRepository.findById(jobId);
        if(taskModelOptional.isPresent()){
            TaskDetailModel taskModel = taskModelOptional.get();
            taskModel.setStatus(status);
            String updatedFailureDetails = StringUtils.hasLength(taskModel.getFailureDetails()) ?
                    (taskModel.getFailureDetails() + " and " + failureDetails) : failureDetails;
            taskModel.setFailureDetails(updatedFailureDetails);
            taskDetailModelRepository.save(taskModel);
        } else{
            log.info("No job found with given jobId: {}", jobId);
        }
    }

    private int generateNewTaskRecord(String task){
        TaskDetailModel taskModel = new TaskDetailModel();
        taskModel.setTask(task);
        taskModel.setStatus(JobStatus.inProgress.toString());
        taskDetailModelRepository.save(taskModel);
        return taskModel.getJobId();
    }
}
