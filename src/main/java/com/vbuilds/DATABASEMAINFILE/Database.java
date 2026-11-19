package com.vbuilds.DATABASEMAINFILE;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class Database {
    private static volatile HikariDataSource dataSource;

    private Database() {}

    private static String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }

    private static HikariDataSource pool() {
        HikariDataSource current = dataSource;
        if (current == null) {
            synchronized (Database.class) {
                current = dataSource;
                if (current == null) {
                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl(setting("DB_URL", "jdbc:postgresql://localhost:5432/VBBuilds"));
                    config.setUsername(setting("DB_USER", "postgres"));
                    config.setPassword(setting("DB_PASSWORD", System.getenv("PASSWORD")));
                    config.setMaximumPoolSize(Integer.parseInt(setting("DB_POOL_SIZE", "5")));
                    config.setMinimumIdle(0);
                    config.setConnectionTimeout(10000);
                    config.setPoolName("VBuildsPool");
                    current = new HikariDataSource(config);
                    dataSource = current;
                }
            }
        }
        return current;
    }

    public static Connection connect() throws SQLException {
        return pool().getConnection();
    }
}
