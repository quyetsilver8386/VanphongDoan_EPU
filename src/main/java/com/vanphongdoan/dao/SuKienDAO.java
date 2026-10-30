package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.SuKien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuKienDAO {

    public List<SuKien> getAllSuKien() {
        List<SuKien> list = new ArrayList<>();
        String sql = "SELECT ID_SuKien, TenSuKien, CONVERT(VARCHAR(10), NgayToChuc, 120) AS NgayToChuc, DiaDiem, ISNULL(KinhPhi, 0) AS KinhPhi, TrangThai, ISNULL(DanhSachViTri, N'MC, Lễ tân, Chạy sự kiện, Hậu cần') AS DanhSachViTri FROM SuKien ORDER BY NgayToChuc DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SuKien sk = new SuKien();
                sk.setIdSuKien(rs.getInt("ID_SuKien"));
                sk.setTenSuKien(rs.getString("TenSuKien"));
                sk.setNgayToChuc(rs.getString("NgayToChuc"));
                sk.setDiaDiem(rs.getString("DiaDiem"));
                sk.setKinhPhi(rs.getDouble("KinhPhi"));
                sk.setTrangThai(rs.getString("TrangThai"));
                sk.setDanhSachViTri(rs.getString("DanhSachViTri"));
                list.add(sk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<SuKien> getAllSuKienForUser(String maSV) {
        List<SuKien> list = new ArrayList<>();
        String sql = "SELECT sk.ID_SuKien, sk.TenSuKien, CONVERT(VARCHAR(10), sk.NgayToChuc, 120) AS NgayToChuc, " +
                     "sk.DiaDiem, ISNULL(sk.KinhPhi, 0) AS KinhPhi, sk.TrangThai, ISNULL(sk.DanhSachViTri, N'MC, Lễ tân, Chạy sự kiện, Hậu cần') AS DanhSachViTri, dk.ViTri, dk.TrangThai AS TrangThaiDangKy " +
                     "FROM SuKien sk " +
                     "LEFT JOIN DangKySuKien dk ON sk.ID_SuKien = dk.ID_SuKien AND dk.MaSV = ? " +
                     "ORDER BY sk.NgayToChuc DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SuKien sk = new SuKien();
                    sk.setIdSuKien(rs.getInt("ID_SuKien"));
                    sk.setTenSuKien(rs.getString("TenSuKien"));
                    sk.setNgayToChuc(rs.getString("NgayToChuc"));
                    sk.setDiaDiem(rs.getString("DiaDiem"));
                    sk.setKinhPhi(rs.getDouble("KinhPhi"));
                    sk.setTrangThai(rs.getString("TrangThai"));
                    sk.setDanhSachViTri(rs.getString("DanhSachViTri"));
                    sk.setViTriDangKy(rs.getString("ViTri"));
                    sk.setTrangThaiDangKy(rs.getString("TrangThaiDangKy"));
                    list.add(sk);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi truy vấn CSDL: " + e.getMessage());
        }
        return list;
    }

    public boolean insertSuKien(SuKien sk) {
        String sql = "INSERT INTO SuKien (TenSuKien, NgayToChuc, DiaDiem, KinhPhi, TrangThai, DanhSachViTri) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sk.getTenSuKien());
            ps.setString(2, sk.getNgayToChuc());
            ps.setString(3, sk.getDiaDiem());
            ps.setDouble(4, sk.getKinhPhi());
            ps.setString(5, sk.getTrangThai());
            ps.setString(6, sk.getDanhSachViTri());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSuKien(SuKien sk) {
        String sql = "UPDATE SuKien SET TenSuKien = ?, NgayToChuc = ?, DiaDiem = ?, KinhPhi = ?, TrangThai = ?, DanhSachViTri = ? WHERE ID_SuKien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sk.getTenSuKien());
            ps.setString(2, sk.getNgayToChuc());
            ps.setString(3, sk.getDiaDiem());
            ps.setDouble(4, sk.getKinhPhi());
            ps.setString(5, sk.getTrangThai());
            ps.setString(6, sk.getDanhSachViTri());
            ps.setInt(7, sk.getIdSuKien());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSuKien(int idSuKien) {
        String sql = "DELETE FROM SuKien WHERE ID_SuKien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSuKien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean dangKySuKien(String maSV, int idSuKien, String viTri) {
        String sql = "INSERT INTO DangKySuKien (ID_SuKien, MaSV, ViTri, TrangThai, NgayDangKy) VALUES (?, ?, ?, N'Đã đăng ký', GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSuKien);
            ps.setString(2, maSV);
            ps.setString(3, viTri);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xinHuySuKien(String maSV, int idSuKien, String lyDo, String minhChungPath) {
        String sql = "UPDATE DangKySuKien SET TrangThai = N'Chờ hủy', LyDoHuy = ?, MinhChung = ? WHERE ID_SuKien = ? AND MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lyDo);
            ps.setString(2, minhChungPath);
            ps.setInt(3, idSuKien);
            ps.setString(4, maSV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean huyDangKySuKien(String maSV, int idSuKien) {
        String sql = "DELETE FROM DangKySuKien WHERE ID_SuKien = ? AND MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSuKien);
            ps.setString(2, maSV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}