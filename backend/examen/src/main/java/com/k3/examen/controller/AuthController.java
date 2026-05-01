package com.k3.examen.controller;

import com.k3.examen.config.JwtUtil;
import com.k3.examen.dto.LoginRequest;
import com.k3.examen.dto.LoginResponse;
import com.k3.examen.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    //  Me — retourne l'admin connecté (pour Dev 4)
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        return ResponseEntity.ok(Map.of("username", username));
    }
}