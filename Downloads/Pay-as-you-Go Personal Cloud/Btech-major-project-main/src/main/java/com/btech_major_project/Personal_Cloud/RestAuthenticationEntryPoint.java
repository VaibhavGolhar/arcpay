package com.btech_major_project.Personal_Cloud;

import com.btech_major_project.Personal_Cloud.dto.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final AppLogger log = AppLogger.getLogger(RestAuthenticationEntryPoint.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.warn("401 Unauthorized: method=" + request.getMethod() + ", uri=" + request.getRequestURI() + ", msg=" + authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiError error = new ApiError("UNAUTHORIZED", "Authentication required", authException.getMessage());
        mapper.writeValue(response.getOutputStream(), error);
    }
}
