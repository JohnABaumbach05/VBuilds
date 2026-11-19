package com.vbuilds.DATABASEMAINFILE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    public static Connection connect() throws SQLException {
        String url = setting("DB_URL", "jdbc:postgresql://localhost:5432/VBBuilds");
        String username = setting("DB_USER", "postgres");
        String password = setting("DB_PASSWORD", System.getenv("PASSWORD"));
        return DriverManager.getConnection(url, username, password);
    }
}
