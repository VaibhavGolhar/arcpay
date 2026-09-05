package com.btech_major_project.Personal_Cloud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<FileMetadata> findByIdAndUserId(Long id, Long userId);
}
