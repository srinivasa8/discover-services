package com.example.app.exception;

public class InvalidInputException extends Exception{
    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}
