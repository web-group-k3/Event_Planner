package com.k3.examen.repository.impl;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Admin;
import com.k3.examen.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class AdminRepositoryImpl implements AdminRepository {
    @Autowired
    private DataSource dataSource;
    @Override
    public Optional<Admin> findByUsername(String username) {
        String sql = "SELECT id, username, password FROM admins WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Admin.builder()
                            .id(rs.getLong("id"))
                            .username(rs.getString("username"))
                            .passwordHash(rs.getString("password"))
                            .build());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eror findByUsername admin", e);
        }
        return Optional.empty();
    }
    }
