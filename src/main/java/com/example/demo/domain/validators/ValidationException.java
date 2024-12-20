package com.example.demo.domain.validators;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
