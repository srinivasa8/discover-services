package com.example.app.common;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
    private String status;
    private String error;
    private String message;
}
