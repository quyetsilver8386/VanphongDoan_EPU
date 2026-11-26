package com.vanphongdoan.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    // Thay 'localhost' bằng '127.0.0.1' để bỏ qua độ trễ phân giải DNS và IPv6
    private static final String URL = "jdbc:sqlserver://127.0.0.1;instanceName=SQLEXPRESS;databaseName=QL_VanPhongDoan;user=admin_epu;password=123;encrypt=true;trustServerCertificate=true;loginTimeout=3;";

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            System.err.println(">>> LỖI KẾT NỐI CSDL: " + e.getMessage());
            return null;
        }
    }
}