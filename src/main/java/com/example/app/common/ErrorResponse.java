package com.example.app.common;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
//@NoArgsConstructor
public class ErrorResponse {
    public String error;
    public String errorMessage;
}
