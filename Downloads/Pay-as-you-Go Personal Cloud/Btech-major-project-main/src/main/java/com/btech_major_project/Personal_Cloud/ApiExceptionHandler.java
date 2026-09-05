package com.btech_major_project.Personal_Cloud;

import com.btech_major_project.Personal_Cloud.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ControllerAdvice
public class ApiExceptionHandler {

    private static final AppLogger log = AppLogger.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgument: " + ex.getMessage());
        ApiError error = new ApiError("BAD_REQUEST", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation error: " + ex.getMessage());
        ApiError error = new ApiError("VALIDATION_ERROR", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler({MultipartException.class})
    public ResponseEntity<ApiError> handleMultipart(MultipartException ex) {
        log.warn("Multipart error: " + ex.getMessage());
        ApiError error = new ApiError("MULTIPART_ERROR", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiError> handleMissingPart(MissingServletRequestPartException ex) {
        log.warn("Missing part: " + ex.getRequestPartName());
        ApiError error = new ApiError("MISSING_PART", ex.getRequestPartName() + " part is required", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUpload(MaxUploadSizeExceededException ex) {
        log.warn("Payload too large: " + ex.getMessage());
        ApiError error = new ApiError("PAYLOAD_TOO_LARGE", "Uploaded file is too large", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ApiError> handleS3(S3Exception ex) {
        String details = ex.awsErrorDetails() != null ? ex.awsErrorDetails().errorMessage() : null;
        log.error("S3 error: " + ex.getMessage() + (details != null ? (" details=" + details) : ""), ex);
        ApiError error = new ApiError("S3_ERROR", ex.getMessage(), details);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        log.error("Unhandled error: " + ex.getMessage(), ex);
        ApiError error = new ApiError("INTERNAL_ERROR", ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
