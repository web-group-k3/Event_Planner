package com.k3.examen.controller;

import com.k3.examen.config.JwtUtil;
import com.k3.examen.dto.LoginRequest;
import com.k3.examen.dto.LoginResponse;
import com.k3.examen.service.impl.AdminDetailsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AdminDetailsService adminDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(
            AdminDetailsService adminDetailsService,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.adminDetailsService = adminDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest loginRequest
    ) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse("Identifiants incorrects"));
        }

        UserDetails userDetails =
                adminDetailsService.loadUserByUsername(loginRequest.getUsername());

        String token = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof UserDetails)) {

            return ResponseEntity.status(401).build();
        }

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities()
                        .stream()
                        .map(a -> a.getAuthority())
                        .toList(),
                "authenticated", true
        ));
    }
}