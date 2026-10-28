package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.SuKien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SuKienDAO {

    public List<SuKien> getAllSuKien() {
        List<SuKien> list = new ArrayList<>();
        String sql = "SELECT ID_SuKien, TenSuKien, CONVERT(VARCHAR(10), NgayToChuc, 120) AS NgayToChuc, DiaDiem, ISNULL(KinhPhi, 0) AS KinhPhi, TrangThai FROM SuKien ORDER BY NgayToChuc DESC";
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
                list.add(sk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertSuKien(SuKien sk) {
        // ID_SuKien tự tăng nên không đưa vào câu lệnh INSERT
        String sql = "INSERT INTO SuKien (TenSuKien, NgayToChuc, DiaDiem, KinhPhi, TrangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sk.getTenSuKien());
            ps.setString(2, sk.getNgayToChuc());
            ps.setString(3, sk.getDiaDiem());
            ps.setDouble(4, sk.getKinhPhi());
            ps.setString(5, sk.getTrangThai());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSuKien(SuKien sk) {
        String sql = "UPDATE SuKien SET TenSuKien = ?, NgayToChuc = ?, DiaDiem = ?, KinhPhi = ?, TrangThai = ? WHERE ID_SuKien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sk.getTenSuKien());
            ps.setString(2, sk.getNgayToChuc());
            ps.setString(3, sk.getDiaDiem());
            ps.setDouble(4, sk.getKinhPhi());
            ps.setString(5, sk.getTrangThai());
            ps.setInt(6, sk.getIdSuKien());
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
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}