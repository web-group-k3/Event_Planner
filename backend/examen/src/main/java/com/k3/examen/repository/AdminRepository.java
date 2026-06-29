package com.k3.examen.repository;

import com.k3.examen.model.Admin;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository {
    Optional<Admin> findByUsername(String username);
}
