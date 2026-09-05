package com.btech_major_project.Personal_Cloud;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "file_metadata", indexes = {
        @Index(name = "idx_file_user", columnList = "user_id"),
        @Index(name = "idx_file_key", columnList = "s3_key")
})
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owner
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_file_user"))
    private User user;

    // Full S3 object key including user's prefix
    @Column(name = "s3_key", nullable = false, length = 1024)
    private String s3Key;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "etag")
    private String etag;

    @Column(name = "version_id")
    private String versionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and setters

    public Long getId() { return id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getS3Key() { return s3Key; }

    public void setS3Key(String s3Key) { this.s3Key = s3Key; }

    public String getFilename() { return filename; }

    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }

    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getSizeBytes() { return sizeBytes; }

    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    public String getEtag() { return etag; }

    public void setEtag(String etag) { this.etag = etag; }

    public String getVersionId() { return versionId; }

    public void setVersionId(String versionId) { this.versionId = versionId; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
}
