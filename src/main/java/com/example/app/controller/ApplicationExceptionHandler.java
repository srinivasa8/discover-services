package com.example.app.controller;

import com.example.app.common.ErrorResponse;
import com.example.app.controller.exception.InvalidInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.app.common.Constants.*;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(Exception.class)
    String handlerException(Exception e){
        System.out.println("========-->"+e);
        return e.getMessage();
    }
   @ExceptionHandler(MissingServletRequestParameterException.class)
   ResponseEntity<ErrorResponse> handlerMissingServletRequestParameterException(MissingServletRequestParameterException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(MISSING_PARAMETER_ERROR);
        errorResponse.setErrorMessage(MISSING_PARAMETER_ERROR_MESSAGE);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    ResponseEntity<ErrorResponse> handlerInvalidInputException(InvalidInputException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(INVALID_INPUT_ERROR);
        errorResponse.setErrorMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
