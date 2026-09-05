package com.btech_major_project.Personal_Cloud;

import com.btech_major_project.Personal_Cloud.dto.FileDownloadResult;
import com.btech_major_project.Personal_Cloud.dto.FileInfoResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final AppLogger log = AppLogger.getLogger(FileController.class);

    private final StorageService storageService;
    private final UserService userService;

    public FileController(StorageService storageService, UserService userService) {
        this.storageService = storageService;
        this.userService = userService;
    }

    private User currentUser(UserDetails principal) {
        return userService.findByEmail(principal.getUsername());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileInfoResponse> upload(@AuthenticationPrincipal UserDetails principal,
                                                   @RequestParam("file") MultipartFile file,
                                                   @RequestParam(value = "path", required = false) String path) throws IOException {
        log.info("POST /api/files upload requested by=" + (principal != null ? principal.getUsername() : "anonymous") +
                ", filename=" + file.getOriginalFilename() + ", path=" + (path == null ? "" : path));
        User user = currentUser(principal);
        FileMetadata meta = storageService.upload(user, file, path);
        log.info("Upload success userId=" + user.getId() + ", fileId=" + meta.getId() + ", key=" + meta.getS3Key());
        return ResponseEntity.ok(new FileInfoResponse(
                meta.getId(), meta.getFilename(), meta.getContentType(), meta.getSizeBytes(), meta.getCreatedAt()
        ));
    }

    @GetMapping
    public ResponseEntity<List<FileInfoResponse>> list(@AuthenticationPrincipal UserDetails principal) {
        log.info("GET /api/files list requested by=" + (principal != null ? principal.getUsername() : "anonymous"));
        User user = currentUser(principal);
        List<FileInfoResponse> list = storageService.list(user).stream()
                .map(m -> new FileInfoResponse(m.getId(), m.getFilename(), m.getContentType(), m.getSizeBytes(), m.getCreatedAt()))
                .toList();
        log.info("List success userId=" + user.getId() + ", count=" + list.size());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@AuthenticationPrincipal UserDetails principal,
                                                        @PathVariable Long id) {
        log.info("GET /api/files/" + id + "/download requested by=" + (principal != null ? principal.getUsername() : "anonymous"));
        User user = currentUser(principal);
        FileDownloadResult res = storageService.download(user, id);

        String encoded = URLEncoder.encode(res.getFilename(), StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(encoded).build());
        headers.setContentType(res.getContentType() != null ?
                MediaType.parseMediaType(res.getContentType()) :
                MediaType.APPLICATION_OCTET_STREAM);

        log.info("Download prepared userId=" + user.getId() + ", fileId=" + id + ", status=200");
        return new ResponseEntity<>(res.getResource(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserDetails principal,
                                       @PathVariable Long id) {
        log.info("DELETE /api/files/" + id + " requested by=" + (principal != null ? principal.getUsername() : "anonymous"));
        User user = currentUser(principal);
        storageService.delete(user, id);
        log.info("Delete success userId=" + user.getId() + ", fileId=" + id);
        return ResponseEntity.noContent().build();
    }
}
