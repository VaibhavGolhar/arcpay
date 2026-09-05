package com.btech_major_project.Personal_Cloud;

import com.btech_major_project.Personal_Cloud.dto.LoginRequest;
import com.btech_major_project.Personal_Cloud.dto.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = new User();
        u.setEmail(req.getEmail());
        u.setFullName(req.getFullName());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        // Non‑guessable per‑user S3 prefix
        u.setS3Prefix("u-" + UUID.randomUUID() + "/");
        userRepository.save(u);
        return jwtService.generateToken(u.getEmail());
    }

    public String login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        return jwtService.generateToken(req.getEmail());
    }
}
