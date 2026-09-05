package com.btech_major_project.Personal_Cloud.dto;

import org.springframework.core.io.InputStreamResource;

public class FileDownloadResult {
    private final InputStreamResource resource;
    private final String filename;
    private final String contentType;

    public FileDownloadResult(InputStreamResource resource, String filename, String contentType) {
        this.resource = resource;
        this.filename = filename;
        this.contentType = contentType;
    }

    public InputStreamResource getResource() {
        return resource;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }
}
