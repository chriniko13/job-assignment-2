package com.chriniko.job.assignment.error.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
class ErrorDetails {
    private final Date timestamp;
    private final String message;
    private final String details;
}
