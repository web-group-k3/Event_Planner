package com.k3.examen.service;

import com.k3.examen.dto.LoginRequest;
import com.k3.examen.dto.LoginResponse;
import com.k3.examen.model.Admin;
import com.k3.examen.repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.k3.examen.config.JwtUtil
public class AuthService {
    private final AdminRepository adminRepository = new AdminRepository();
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest loginRequest) {
        Admin admin= adminRepository.getAdmin(loginRequest.getUsername());
        if(admin==null){
            throw new RuntimeException("Information incorrect");
        }
        boolean passwordMatch =bCryptPasswordEncoder.matches(
                loginRequest.getPassword(),
                admin.getPasswordHash()
        );
        if(passwordMatch){
            throw new RuntimeException("Information Incorrect");
        }
        String token =JwtUtil.generateToken(admin.getUsername());
        return new LoginResponse(token);
    }
}
