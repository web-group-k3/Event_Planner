package com.k3.examen.service;

import com.k3.examen.dto.LoginRequest;
import com.k3.examen.dto.LoginResponse;
import com.k3.examen.model.Admin;
import com.k3.examen.repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.k3.examen.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Génère automatiquement le constructeur pour les champs 'final'
public class AuthService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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

        String token = JwtUtil.generateToken(admin.getUsername());
        return new LoginResponse(token);
    }
}