package com.example.app.exception;

import com.example.app.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.app.common.Constants.*;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleException(Exception e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.name());
        errorResponse.setError(GENERIC_ERROR);
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

   @ExceptionHandler(MissingServletRequestParameterException.class)
   ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.name());
        errorResponse.setError(MISSING_PARAMETER_ERROR);
        errorResponse.setMessage(MISSING_PARAMETER_ERROR_MESSAGE);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    ResponseEntity<ErrorResponse> handleInvalidInputException(InvalidInputException e){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.name());
        errorResponse.setError(INVALID_INPUT_ERROR);
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
