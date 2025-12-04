package com.recruitment.app.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/recruitment_db";
    private static final String USER = "postgres";   // your PG username
    private static final String PASSWORD = "kashafali471"; // your PG password

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot connect to PostgreSQL DB!", ex);
        }
    }
}
