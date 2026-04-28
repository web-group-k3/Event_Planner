package com.k3.examen.repository;

import com.k3.examen.config.DatabaseConnection;
import com.k3.examen.model.Admin;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AdminRepository {
    public Admin getAdmin(String username) {
        String sql = "SELECT id, username, password_hash FROM admin WHERE username = ?";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPasswordHash(rs.getString("password_hash"));
                return admin;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'admin: " + e.getMessage());
        }

        return null;
    }
}