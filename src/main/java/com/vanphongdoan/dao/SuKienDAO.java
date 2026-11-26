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
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try (PreparedStatement ps = conn.prepareStatement(sql);
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(SuKien sk) {
        String sql = "INSERT INTO SuKien (TenSuKien, NgayToChuc, DiaDiem, KinhPhi, ViTriTuyen, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, sk.getTenSuKien());
                ps.setString(2, sk.getNgayToChuc());
                ps.setString(3, sk.getDiaDiem());
                ps.setDouble(4, sk.getKinhPhi());
                ps.setString(5, sk.getViTriTuyen());
                ps.setString(6, sk.getTrangThai());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(SuKien sk) {
        String sql = "UPDATE SuKien SET TenSuKien = ?, NgayToChuc = ?, DiaDiem = ?, KinhPhi = ?, ViTriTuyen = ?, TrangThai = ? WHERE ID_SuKien = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, sk.getTenSuKien());
                ps.setString(2, sk.getNgayToChuc());
                ps.setString(3, sk.getDiaDiem());
                ps.setDouble(4, sk.getKinhPhi());
                ps.setString(5, sk.getViTriTuyen());
                ps.setString(6, sk.getTrangThai());
                ps.setInt(7, sk.getIdSuKien());
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int idSuKien) {
        String sql = "DELETE FROM SuKien WHERE ID_SuKien = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idSuKien);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean insertSuKien(SuKien sk) {
        String sql = "INSERT INTO SuKien (TenSuKien, NgayToChuc, DiaDiem, KinhPhi, TrangThai, ViTriTuyen) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = com.vanphongdoan.config.DatabaseConnection.getConnection();
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
        try (Connection conn = com.vanphongdoan.config.DatabaseConnection.getConnection();
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
        try (Connection conn = com.vanphongdoan.config.DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSuKien);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}