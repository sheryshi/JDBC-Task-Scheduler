package com.scheduler.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionUtil {
    
    // Use the standard format pointing to the database schema
    private static final String DB_URL = "jdbc:mysql://localhost:3306/task_schedulaer_db"; 
    
    // Credentials are correct for your configuration
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = "root";

    /**
     * Establishes and returns a new database connection.
     * @return A valid Connection object.
     * @throws SQLException if the connection cannot be established.
     */
    public static Connection getConnection() throws SQLException {
        
        System.out.println("Attempting to connect to the database...");
        // DriverManager.getConnection uses the separate DB_URL, DB_USER, DB_PASSWORD arguments
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); 
    }
}