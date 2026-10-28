package com.vanphongdoan.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    private static Connection conn = null;

    public static synchronized Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Properties props = new Properties();

                // Đọc file db.properties
                try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (input != null) {
                        props.load(input);
                    }
                } catch (Exception ignored) {}

                // Ưu tiên đọc từ db.properties, nếu không thấy sẽ dùng chuỗi localhost:1433 này
                String url = props.getProperty("db.url", "jdbc:sqlserver://localhost:1433;databaseName=QL_VanPhongDoan;encrypt=false;trustServerCertificate=true;loginTimeout=15;");
                String user = props.getProperty("db.user", "sa");
                String pass = props.getProperty("db.pass", "123456");

                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                conn = DriverManager.getConnection(url, user, pass);
            }
        } catch (Exception e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }
}