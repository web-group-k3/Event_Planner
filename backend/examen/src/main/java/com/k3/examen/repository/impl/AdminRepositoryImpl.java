package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Admin;
import com.k3.examen.repository.AdminRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class AdminRepositoryImpl implements AdminRepository {

    @Override
    public Optional<Admin> findByUsername(String username) {
        String sql = "SELECT id, username, password_hash FROM admin WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Admin.builder()
                            .id(rs.getLong("id"))
                            .username(rs.getString("username"))
                            .passwordHash(rs.getString("password_hash"))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error findByUsername admin: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
