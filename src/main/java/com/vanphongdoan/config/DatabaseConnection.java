package com.vanphongdoan.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static Connection conn = null;

    public static synchronized Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                
                // Kết nối qua cổng mặc định và tài khoản sa (không cần DLL)
                String url = "jdbc:sqlserver://127.0.0.1\\SQLEXPRESS;databaseName=QL_VanPhongDoan;encrypt=false;trustServerCertificate=true;loginTimeout=3;";
                String user = "sa";
                String pass = "123456"; // Đổi thành mật khẩu sa trên máy của bạn
                
                conn = DriverManager.getConnection(url, user, pass);
            }
        } catch (Exception e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}