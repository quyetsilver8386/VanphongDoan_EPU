package com.vanphongdoan.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS01:1433;databaseName=QL_VanPhongDoan;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa"; 
    private static final String PASSWORD = "123456"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Loi ket noi CSDL: " + e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Ket noi SQL Server QL_VanPhongDoan thanh cong!");
        } else {
            System.out.println("Ket noi thất bại!");
        }
    }
}