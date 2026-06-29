package com.k3.examen.config;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnection {

    private static final String URL = System.getenv("DB_URL");
    private static final String USERNAME = System.getenv("DB_USERNAME");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DRIVER = "org.postgresql.Driver";

    public static Connection getConnection() {

        try {
            Class.forName(DRIVER);

            System.out.println(URL);
            System.out.println(USERNAME);
            System.out.println(PASSWORD);

            return DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (Exception e) {
            throw new RuntimeException("Erreur connexion DB : " + e.getMessage());
        }
    }
}