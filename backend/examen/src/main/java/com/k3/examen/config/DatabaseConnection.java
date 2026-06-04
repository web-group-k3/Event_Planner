package com.k3.examen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class DatabaseConnection {

    private static final String DRIVER = "org.postgresql.Driver";

    // Lus depuis application.properties (spring.datasource.*)
    private static String url;
    private static String username;
    private static String password;

    // Spring injecte les valeurs via les setters statiques
    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        DatabaseConnection.url = url;
    }

    @Value("${spring.datasource.username}")
    public void setUsername(String username) {
        DatabaseConnection.username = username;
    }

    @Value("${spring.datasource.password}")
    public void setPassword(String password) {
        DatabaseConnection.password = password;
    }

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Erreur connexion DB : " + e.getMessage(), e);
        }
    }
}
