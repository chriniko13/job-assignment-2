package com.chriniko.job.assignment.error;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
