package com.btech_major_project.Personal_Cloud;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_usage", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_usage_user", columnNames = {"user_id"})
})
public class UserUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_usage_user"))
    private User user;

    @Column(name = "total_bytes_stored", nullable = false)
    private long totalBytesStored = 0L;

    @Column(name = "object_count", nullable = false)
    private long objectCount = 0L;

    // Operation counters
    @Column(name = "put_count", nullable = false)
    private long putCount = 0L;

    @Column(name = "copy_count", nullable = false)
    private long copyCount = 0L;

    @Column(name = "post_count", nullable = false)
    private long postCount = 0L;

    @Column(name = "list_count", nullable = false)
    private long listCount = 0L;

    @Column(name = "get_count", nullable = false)
    private long getCount = 0L;

    @Column(name = "select_count", nullable = false)
    private long selectCount = 0L;

    @Column(name = "delete_count", nullable = false)
    private long deleteCount = 0L;

    @Column(name = "other_count", nullable = false)
    private long otherCount = 0L;

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

    // getters and setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public long getTotalBytesStored() { return totalBytesStored; }
    public void setTotalBytesStored(long totalBytesStored) { this.totalBytesStored = totalBytesStored; }

    public long getObjectCount() { return objectCount; }
    public void setObjectCount(long objectCount) { this.objectCount = objectCount; }

    public long getPutCount() { return putCount; }
    public void setPutCount(long putCount) { this.putCount = putCount; }

    public long getCopyCount() { return copyCount; }
    public void setCopyCount(long copyCount) { this.copyCount = copyCount; }

    public long getPostCount() { return postCount; }
    public void setPostCount(long postCount) { this.postCount = postCount; }

    public long getListCount() { return listCount; }
    public void setListCount(long listCount) { this.listCount = listCount; }

    public long getGetCount() { return getCount; }
    public void setGetCount(long getCount) { this.getCount = getCount; }

    public long getSelectCount() { return selectCount; }
    public void setSelectCount(long selectCount) { this.selectCount = selectCount; }

    public long getDeleteCount() { return deleteCount; }
    public void setDeleteCount(long deleteCount) { this.deleteCount = deleteCount; }

    public long getOtherCount() { return otherCount; }
    public void setOtherCount(long otherCount) { this.otherCount = otherCount; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}

