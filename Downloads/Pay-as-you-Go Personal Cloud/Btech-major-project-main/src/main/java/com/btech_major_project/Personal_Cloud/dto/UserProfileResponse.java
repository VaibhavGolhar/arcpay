package com.btech_major_project.Personal_Cloud.dto;

/**
 * Response DTO for the /api/auth/me endpoint.
 * Exposes only the fields a client needs — deliberately excludes
 * sensitive internals such as the user's S3 prefix.
 */
public class UserProfileResponse {

    private final Long id;
    private final String email;
    private final String fullName;

    public UserProfileResponse(Long id, String email, String fullName) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
}
