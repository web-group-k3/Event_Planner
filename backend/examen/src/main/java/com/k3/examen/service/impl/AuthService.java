package com.k3.examen.service.impl;

import com.k3.examen.config.JwtUtil;
import com.k3.examen.dto.LoginRequest;
import com.k3.examen.dto.LoginResponse;
import com.k3.examen.model.Admin;
import com.k3.examen.repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AuthService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;  // ✅ Injecté, plus appelé en static

    public AuthService(AdminRepository adminRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Admin admin = adminRepository.getAdmin(loginRequest.getUsername());

        if (admin == null) {
            throw new RuntimeException("Information incorrect");
        }

        boolean passwordMatch = bCryptPasswordEncoder.matches(
                loginRequest.getPassword(),
                admin.getPasswordHash()
        );

        if (!passwordMatch) {
            throw new RuntimeException("Information incorrect");
        }

        String token = jwtUtil.generateToken(admin.getUsername());  // ✅ instance, pas static
        return new LoginResponse(token);
    }
}