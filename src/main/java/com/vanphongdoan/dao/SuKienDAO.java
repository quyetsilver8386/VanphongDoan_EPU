package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.SuKien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SuKienDAO {

    public List<SuKien> getAllSuKien() {
        List<SuKien> list = new ArrayList<>();
        String sql = "SELECT * FROM SuKien ORDER BY ID_SuKien ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SuKien sk = new SuKien();
                sk.setIdSuKien(rs.getInt("ID_SuKien"));
                sk.setTenSuKien(rs.getString("TenSuKien"));
                sk.setNgayToChuc(rs.getString("NgayToChuc"));
                sk.setDiaDiem(rs.getString("DiaDiem"));
                try { sk.setKinhPhi(rs.getDouble("KinhPhi")); } catch (Exception ignored) {}
                try { sk.setViTriTuyen(rs.getString("ViTriTuyen")); } catch (Exception ignored) {}
                sk.setTrangThai(rs.getString("TrangThai"));
                list.add(sk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertSuKien(SuKien sk) {
        String sql = "INSERT INTO SuKien (TenSuKien, NgayToChuc, DiaDiem, KinhPhi, TrangThai, ViTriTuyen) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sk.getTenSuKien());
            ps.setString(2, sk.getNgayToChuc());
            ps.setString(3, sk.getDiaDiem());
            ps.setDouble(4, sk.getKinhPhi());
            ps.setString(5, sk.getTrangThai());
            ps.setString(6, sk.getViTriTuyen());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSuKien(SuKien sk) {
        String sql = "UPDATE SuKien SET TenSuKien = ?, NgayToChuc = ?, DiaDiem = ?, KinhPhi = ?, TrangThai = ?, ViTriTuyen = ? WHERE ID_SuKien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sk.getTenSuKien());
            ps.setString(2, sk.getNgayToChuc());
            ps.setString(3, sk.getDiaDiem());
            ps.setDouble(4, sk.getKinhPhi());
            ps.setString(5, sk.getTrangThai());
            ps.setString(6, sk.getViTriTuyen());
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

    // Các phương thức phục vụ giao diện Thành viên (User)
    public List<SuKien> getAllSuKienForUser(String maSV) {
        List<SuKien> list = new ArrayList<>();
        // Lấy toàn bộ sự kiện từ bảng SuKien trước
        String sql = "SELECT * FROM SuKien ORDER BY NgayToChuc DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SuKien sk = new SuKien();
                int idSK = rs.getInt("ID_SuKien");
                sk.setIdSuKien(idSK);
                sk.setTenSuKien(rs.getString("TenSuKien"));
                sk.setNgayToChuc(rs.getString("NgayToChuc"));
                sk.setDiaDiem(rs.getString("DiaDiem"));
                try { sk.setKinhPhi(rs.getDouble("KinhPhi")); } catch (Exception ignored) {}
                sk.setTrangThai(rs.getString("TrangThai"));
                
                String viTri = "";
                try { viTri = rs.getString("ViTriTuyen"); } catch (Exception ignored) {}
                if (viTri == null || viTri.isEmpty()) {
                    try { viTri = rs.getString("DanhSachViTri"); } catch (Exception ignored) {}
                }
                sk.setViTriTuyen(viTri);

                // Kiểm tra xem sinh viên này đã đăng ký sự kiện này chưa
                String sqlDK = "SELECT * FROM DangKySuKien WHERE MaSV = ? AND ID_SuKien = ?";
                try (PreparedStatement psDK = conn.prepareStatement(sqlDK)) {
                    psDK.setString(1, maSV);
                    psDK.setInt(2, idSK);
                    try (ResultSet rsDK = psDK.executeQuery()) {
                        if (rsDK.next()) {
                            try { sk.setViTriDangKy(rsDK.getString("ViTriDangKy")); } catch (Exception ignored) {}
                            String ttDK = "";
                            try { ttDK = rsDK.getString("TrangThaiDuyet"); } catch (Exception ignored) {}
                            if (ttDK == null || ttDK.isEmpty()) {
                                try { ttDK = rsDK.getString("TrangThai"); } catch (Exception ignored) {}
                            }
                            sk.setTrangThaiDangKy(ttDK);
                        }
                    }
                } catch (Exception ignored) {}

                list.add(sk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean dangKySuKien(String maSV, int idSuKien, String viTriDangKy) {
        String checkSql = "SELECT COUNT(*) FROM DangKySuKien WHERE MaSV = ? AND ID_SuKien = ?";
        String insertSql = "INSERT INTO DangKySuKien (MaSV, ID_SuKien, ViTriDangKy, TrangThaiDuyet) VALUES (?, ?, ?, N'Chờ duyệt')";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, maSV);
                psCheck.setInt(2, idSuKien);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, maSV);
                psInsert.setInt(2, idSuKien);
                psInsert.setString(3, viTriDangKy);
                return psInsert.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean huyDangKySuKien(String maSV, int idSuKien) {
        String sql = "DELETE FROM DangKySuKien WHERE MaSV = ? AND ID_SuKien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setInt(2, idSuKien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xinHuySuKien(String maSV, int idSuKien, String lyDo, String duongDanMinhChung) {
        String sql = "UPDATE DangKySuKien SET TrangThaiDuyet = N'Chờ hủy' WHERE MaSV = ? AND ID_SuKien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            ps.setInt(2, idSuKien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}