package com.example.app.controller.exception;

public class InvalidInputException extends Exception{
    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}
