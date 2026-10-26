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
    // 1. Hàm Thêm Nhân sự mới
public boolean insert(NhanSu ns) {
    String sql = "INSERT INTO NhanSu (MaSV, MatKhau, HoTen, Lop, SoDienThoai, TrangThai, ID_VaiTro, ID_Ban) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, ns.getMaSV());
        pstmt.setString(2, ns.getMatKhau());
        pstmt.setString(3, ns.getHoTen());
        pstmt.setString(4, ns.getLop());
        pstmt.setString(5, ns.getSoDienThoai());
        pstmt.setBoolean(6, ns.isTrangThai());
        pstmt.setInt(7, ns.getIdVaiTro());
        if (ns.getIdBan() != null) pstmt.setInt(8, ns.getIdBan());
        else pstmt.setNull(8, java.sql.Types.INTEGER);
        
        return pstmt.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

// 2. Hàm Cập nhật thông tin Nhân sự
public boolean update(NhanSu ns) {
    String sql = "UPDATE NhanSu SET HoTen=?, Lop=?, SoDienThoai=?, TrangThai=?, ID_VaiTro=?, ID_Ban=? WHERE MaSV=?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, ns.getHoTen());
        pstmt.setString(2, ns.getLop());
        pstmt.setString(3, ns.getSoDienThoai());
        pstmt.setBoolean(4, ns.isTrangThai());
        pstmt.setInt(5, ns.getIdVaiTro());
        if (ns.getIdBan() != null) pstmt.setInt(6, ns.getIdBan());
        else pstmt.setNull(6, java.sql.Types.INTEGER);
        pstmt.setString(7, ns.getMaSV());
        
        return pstmt.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

// 3. Hàm Xóa (Khóa) Nhân sự
public boolean delete(String maSV) {
    String sql = "DELETE FROM NhanSu WHERE MaSV=?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, maSV);
        return pstmt.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

// 4. Hàm Tìm kiếm theo MaSV hoặc HoTen
public List<NhanSu> search(String keyword) {
    List<NhanSu> list = new ArrayList<>();
    String sql = "SELECT * FROM NhanSu WHERE MaSV LIKE ? OR HoTen LIKE ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, "%" + keyword + "%");
        pstmt.setString(2, "%" + keyword + "%");
        ResultSet rs = pstmt.executeQuery();
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
