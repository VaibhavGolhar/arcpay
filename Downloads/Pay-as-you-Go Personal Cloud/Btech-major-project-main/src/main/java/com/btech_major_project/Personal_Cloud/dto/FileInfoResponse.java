package com.btech_major_project.Personal_Cloud.dto;

import java.time.Instant;

public class FileInfoResponse {
    private Long id;
    private String filename;
    private String contentType;
    private long sizeBytes;
    private Instant createdAt;

    public FileInfoResponse(Long id, String filename, String contentType, long sizeBytes, Instant createdAt) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getFilename() { return filename; }
    public String getContentType() { return contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public Instant getCreatedAt() { return createdAt; }
}
