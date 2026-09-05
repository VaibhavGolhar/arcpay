package com.btech_major_project.Personal_Cloud;

import com.btech_major_project.Personal_Cloud.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final AppLogger log = AppLogger.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        log.info("POST /api/auth/register email=" + req.getEmail());
        String token = authService.register(req);
        log.info("User registered successfully email=" + req.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        log.info("POST /api/auth/login email=" + req.getEmail());
        String token = authService.login(req);
        log.info("User logged in successfully email=" + req.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails principal) {
        log.info("GET /api/auth/me principal=" + (principal != null ? principal.getUsername() : "anonymous"));
        if (principal == null) {
            log.warn("/api/auth/me called without authentication");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError("UNAUTHORIZED", "Authentication required", null));
        }
        User u = userService.findByEmail(principal.getUsername());
        log.info("/api/auth/me resolved user id=" + u.getId());
        return ResponseEntity.ok(new UserProfileResponse(u.getId(), u.getEmail(), u.getFullName()));
    }
}
