package com.example.app.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Response {
    List<String> buckets;
    List<String> instanceIds;
}
