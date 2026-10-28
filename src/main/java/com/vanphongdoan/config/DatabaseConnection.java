package com.vanphongdoan.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static Connection conn = null;

    public static synchronized Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                
                String url = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS;databaseName=QL_VanPhongDoan;encrypt=false;trustServerCertificate=true;loginTimeout=3;";
                String user = "sa";
                String pass = "123456"; // Đổi đúng mật khẩu sa của bạn nếu có thay đổi

                conn = DriverManager.getConnection(url, user, pass);
            }
        } catch (Exception e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}