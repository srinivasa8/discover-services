package com.example.app.controller;

import com.example.app.service.DiscoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
public class DiscoverServiceController {

    @Autowired
    private DiscoverService discoverService;

    @PostMapping("/discoverServices")
    public int discoverServices(@RequestBody List<String> services) {
        return discoverService.discoverServices(services);
    }

    @GetMapping("/getJobResult")
    public String getJobResult(@RequestParam(name = "jobId") int jobId) {
        return discoverService.getJobResult(jobId);
    }

    @GetMapping("/getDiscoveryResult")
    public List<String> getDiscoveryResult(@RequestParam(name = "service") String service) {
        return discoverService.getDiscoveryResult(service);
    }

    @GetMapping("/getS3BucketObjects")
    public int getS3BucketObjects(@RequestParam(name = "bucketName") String bucketName) {
        return discoverService.getS3BucketObjects(bucketName);
    }

    @GetMapping("/getS3BucketObjectCount")
    public int getS3BucketObjectCount(@RequestParam(name = "bucketName") String bucketName) {
        return discoverService.getS3BucketObjectCount(bucketName);
    }

    @GetMapping("/getS3BucketObjectlike")
    public List<String> getS3BucketObjectlike(@RequestParam(name = "bucketName") String bucketName, @RequestParam(name = "pattern") String pattern) {
        return discoverService.getS3BucketObjectlike(bucketName, pattern);
    }
}
