package com.k3.examen.service.impl;

import com.k3.examen.model.Admin;
import com.k3.examen.repository.AdminRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;

    public UserDetailsServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.getAdmin(username);

        if (admin == null) {
            throw new UsernameNotFoundException("Admin non trouvé : " + username);
        }

        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPasswordHash())
                .roles("ADMIN")
                .build();
    }
}