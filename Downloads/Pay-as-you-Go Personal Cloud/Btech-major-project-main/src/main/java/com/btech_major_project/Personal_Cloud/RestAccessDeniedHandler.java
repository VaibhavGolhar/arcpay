package com.btech_major_project.Personal_Cloud;

import com.btech_major_project.Personal_Cloud.dto.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private static final AppLogger log = AppLogger.getLogger(RestAccessDeniedHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.warn("403 Forbidden: method=" + request.getMethod() + ", uri=" + request.getRequestURI() + ", msg=" + accessDeniedException.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiError error = new ApiError("FORBIDDEN", "Access denied", accessDeniedException.getMessage());
        mapper.writeValue(response.getOutputStream(), error);
    }
}
