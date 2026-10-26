package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.NhanSu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
public class NhanSuDAO {
    public List<NhanSu> getAllNhanSu() {
    List<NhanSu> list = new ArrayList<>();
    String sql = "SELECT * FROM NhanSu";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            NhanSu ns = new NhanSu();
            ns.setMaSV(rs.getString("MaSV"));
            ns.setMatKhau(rs.getString("MatKhau"));
            ns.setHoTen(rs.getString("HoTen"));
            ns.setLop(rs.getString("Lop"));
            ns.setSoDienThoai(rs.getString("SoDienThoai"));
            ns.setTrangThai(rs.getBoolean("TrangThai"));
            ns.setIdVaiTro(rs.getInt("ID_VaiTro"));
            
            Object idBanObj = rs.getObject("ID_Ban");
            ns.setIdBan(idBanObj != null ? (Integer) idBanObj : null);
            
            list.add(ns);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

    public NhanSu checkLogin(String maSV, String matKhau) {
        String sql = "SELECT * FROM NhanSu WHERE MaSV = ? AND MatKhau = ? AND TrangThai = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maSV);
            pstmt.setString(2, matKhau);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                NhanSu ns = new NhanSu();
                ns.setMaSV(rs.getString("MaSV"));
                ns.setMatKhau(rs.getString("MatKhau"));
                ns.setHoTen(rs.getString("HoTen"));
                ns.setLop(rs.getString("Lop"));
                ns.setSoDienThoai(rs.getString("SoDienThoai"));
                ns.setTrangThai(rs.getBoolean("TrangThai"));
                ns.setIdVaiTro(rs.getInt("ID_VaiTro"));
                
                // Lấy ID_Ban (Xử lý an toàn trường hợp Admin có ID_Ban = NULL)
                Object idBanObj = rs.getObject("ID_Ban");
                if (idBanObj != null) {
                    ns.setIdBan((Integer) idBanObj);
                } else {
                    ns.setIdBan(null);
                }
                
                return ns;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}