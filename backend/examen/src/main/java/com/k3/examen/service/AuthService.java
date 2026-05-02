package com.k3.examen.service;

import com.k3.examen.config.JwtUtil;
import com.k3.examen.dto.LoginRequest;
import com.k3.examen.dto.LoginResponse;
import com.k3.examen.model.Admin;
import com.k3.examen.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class AuthService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AdminRepository adminRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }
    public LoginResponse login(LoginRequest loginRequest) {
        System.out.println("Tentative de login pour : " + loginRequest.getUsername());

        Admin admin = adminRepository.getAdmin(loginRequest.getUsername());

        System.out.println("Utilisateur trouvé : " + (admin != null ? admin.getUsername() : "NULL"));

        if (admin == null) {
            throw new RuntimeException("Information incorrect");
        }

        boolean passwordMatch = bCryptPasswordEncoder.matches(
                loginRequest.getPassword(),
                admin.getPasswordHash()
        );

        System.out.println("Password match : " + passwordMatch);

        if (!passwordMatch) {
            throw new RuntimeException("Information incorrect");
        }

        String token = jwtUtil.generateToken(admin.getUsername());
        return new LoginResponse(token);
    }

}