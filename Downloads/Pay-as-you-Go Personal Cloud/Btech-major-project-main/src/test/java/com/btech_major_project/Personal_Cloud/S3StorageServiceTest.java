package com.btech_major_project.Personal_Cloud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class S3StorageServiceTest {

    private S3Client s3;
    private FileMetadataRepository fileRepo;
    private UsageService usageService;
    private S3StorageService storageService;

    @BeforeEach
    void setUp() {
        s3 = Mockito.mock(S3Client.class);
        fileRepo = Mockito.mock(FileMetadataRepository.class);
        usageService = Mockito.mock(UsageService.class);
        storageService = new S3StorageService(s3, fileRepo, usageService, "test-bucket");
    }

    @Test
    void testDelete() {
        User user = new User();
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", 1L);

        FileMetadata meta = new FileMetadata();
        org.springframework.test.util.ReflectionTestUtils.setField(meta, "id", 100L);
        meta.setS3Key("prefix/test.txt");
        meta.setSizeBytes(1024L);

        when(fileRepo.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(meta));
        when(s3.deleteObject(any(DeleteObjectRequest.class))).thenReturn(DeleteObjectResponse.builder().build());

        storageService.delete(user, 100L);

        ArgumentCaptor<DeleteObjectRequest> reqCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3).deleteObject(reqCaptor.capture());
        assertEquals("test-bucket", reqCaptor.getValue().bucket());
        assertEquals("prefix/test.txt", reqCaptor.getValue().key());

        verify(fileRepo).delete(meta);
        verify(usageService).onDelete(user, 1024L);
    }
}
