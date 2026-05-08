package com.k3.examen.config;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnection {

    private static final String URL =System.getenv("DB_URL");
    private static final String USERNAME =System.getenv("DB_USERNAME");
    private static final String PASSWORD =System.getenv("DB_PASSWORD");
    private static Connection connection = null;
    private DatabaseConnection() {}

    public static synchronized Connection getConnection() throws SQLException {
        if (URL == null || USERNAME == null || PASSWORD == null) {
            throw new IllegalStateException(
                    "Variables d'environnement DB_URL / DB_USERNAME / DB_PASSWORD manquantes"
            );
        }
        try {
            Class.forName("org.postgresql.Driver"); // chargement explicite du driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver PostgreSQL introuvable", e);
        }
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connexion à la base de données réussie.");
        }
        return connection;
    }
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture : " + e.getMessage());
            }
        }
    }
}
