package com.btech_major_project.Personal_Cloud.dto;

import java.time.Instant;

public class ApiError {
    private final String code;
    private final String message;
    private final Instant timestamp;
    private final String details;

    public ApiError(String code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
    public String getDetails() { return details; }
}

