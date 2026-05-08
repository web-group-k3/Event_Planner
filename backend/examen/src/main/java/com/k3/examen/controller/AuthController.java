package com.k3.examen.controller;

import com.k3.examen.config.JwtUtil;
import com.k3.examen.dto.LoginRequest;
import com.k3.examen.dto.LoginResponse;
import com.k3.examen.service.impl.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    //  Login — retourne un token JWT
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList(),
                "authenticated", true
        ));
    }
}