package com.vbuilds.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL =  "jdbc:postgresql://localhost:5432/VBBuilds";

    private static final String USER = "postgres";

    private static final String PASSWORD = System.getenv("VBUILDS_DB_PASSWORD");


    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
