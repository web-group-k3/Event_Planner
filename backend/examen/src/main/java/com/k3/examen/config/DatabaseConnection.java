package com.k3.examen.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnection {

    private static final String URL =System.getenv("DB_URL");
    private static final String USER =System.getenv("DB_USERNAME");
    private static final String PASSWORD =System.getenv("DB_PASSWORD");
    private static final String DRIVER = "org.postgresql.Driver";
    private static Connection connection = null;

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
        try {
            connection=DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion à la base de données réussie.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            throw new RuntimeException(e);

        }

    }
        return connection;
}
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }}
