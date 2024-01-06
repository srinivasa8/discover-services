package com.example.app.controller;

import com.example.app.common.Request;
import com.example.app.service.DiscoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
public class DiscoverServiceController {

    @Autowired
    private DiscoverService discoverService;

    @PostMapping("/discoverServices")
    public ResponseEntity<Integer> discoverServices(@RequestBody List<String> services) throws Exception {
        int jobId = discoverService.discoverServices(services);
        return new ResponseEntity<>(jobId, HttpStatus.OK);
    }

    @GetMapping("/getJobResult")
    public ResponseEntity<String> getJobResult(@RequestParam(name = "jobId") int jobId) throws Exception {
        String result = discoverService.getJobResult(jobId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getDiscoveryResult")
    public ResponseEntity<List<String>> getDiscoveryResult(@RequestParam(name = "service") String service) throws Exception {
        List<String> resultList = discoverService.getDiscoveryResult(service);
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    @GetMapping("/getS3BucketObjects")
    public ResponseEntity<Integer> getS3BucketObjects(@RequestParam(name = "bucketName") String bucketName) throws Exception {
        int jobId = discoverService.getS3BucketObjects(bucketName);
        return new ResponseEntity<>(jobId, HttpStatus.OK);
    }

    @GetMapping("/getS3BucketObjectCount")
    public ResponseEntity<Integer> getS3BucketObjectCount(@RequestParam(name = "bucketName") String bucketName) throws Exception {
        int count = discoverService.getS3BucketObjectCount(bucketName);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("/getS3BucketObjectLike")
    public ResponseEntity<List<String>> getS3BucketObjectLike(@RequestBody Request request) throws Exception {
        List<String> s3ObjectList = discoverService.getS3BucketObjectLike(request.getBucketName(), request.getPattern());
        return new ResponseEntity<>(s3ObjectList, HttpStatus.OK);
    }

}
