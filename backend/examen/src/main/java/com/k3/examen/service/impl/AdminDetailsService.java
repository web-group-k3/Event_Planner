package com.k3.examen.service.impl;

import com.k3.examen.model.Admin;
import com.k3.examen.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Admin not found: " + username)
                );

        return new org.springframework.security.core.userdetails.User(
                admin.getUsername(),
                admin.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }
}