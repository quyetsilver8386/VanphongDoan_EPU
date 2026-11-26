package com.vanphongdoan.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseConnection {

    private static Connection conn = null;

    private static class DbProfile {
        String url, user, pass;
        DbProfile(String url, String user, String pass) {
            this.url = url;
            this.user = user;
            this.pass = pass;
        }
    }

    public static synchronized Connection getConnection() {
        try {
            if (conn != null && !conn.isClosed() && conn.isValid(2)) {
                return conn;
            }

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            List<DbProfile> profiles = new ArrayList<>();

            // 1. Ưu tiên đọc từ db.properties nếu có
            try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    Properties props = new Properties();
                    props.load(input);
                    String url = props.getProperty("db.url");
                    if (url != null && !url.isEmpty()) {
                        profiles.add(new DbProfile(url, props.getProperty("db.user"), props.getProperty("db.pass")));
                    }
                }
            } catch (Exception ignored) {}

            // 2. Cấu hình máy bạn (SQLEXPRESS01)
            profiles.add(new DbProfile(
                "jdbc:sqlserver://127.0.0.1;instanceName=SQLEXPRESS01;databaseName=QL_VanPhongDoan;encrypt=true;trustServerCertificate=true;loginTimeout=2;",
                "admin_epu", "123"
            ));

            // 3. Cấu hình máy bạn của bạn (port 1433 / SQLEXPRESS)
            profiles.add(new DbProfile(
                "jdbc:sqlserver://127.0.0.1:1433;databaseName=QL_VanPhongDoan;encrypt=true;trustServerCertificate=true;loginTimeout=2;",
                "sa", "123456"
            ));
            profiles.add(new DbProfile(
                "jdbc:sqlserver://127.0.0.1;instanceName=SQLEXPRESS;databaseName=QL_VanPhongDoan;encrypt=true;trustServerCertificate=true;loginTimeout=2;",
                "sa", "123456"
            ));

            Exception lastException = null;
            for (DbProfile p : profiles) {
                try {
                    conn = DriverManager.getConnection(p.url, p.user, p.pass);
                    if (conn != null) {
                        System.out.println(">>> Kết nối CSDL thành công với user: " + p.user);
                        return conn;
                    }
                } catch (Exception e) {
                    lastException = e;
                }
            }

            if (lastException != null) {
                throw lastException;
            }

        } catch (Exception e) {
            System.err.println(">>> LỖI KẾT NỐI CSDL: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}