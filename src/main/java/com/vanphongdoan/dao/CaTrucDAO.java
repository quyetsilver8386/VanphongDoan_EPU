package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.CaTruc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CaTrucDAO {

    // 1. LẤY TOÀN BỘ DANH SÁCH CA TRỰC TỪ SQL SERVER
    public List<CaTruc> getAllCaTruc() {
        List<CaTruc> list = new ArrayList<>();
        String sql = "SELECT * FROM CaTruc ORDER BY NgayTruc DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CaTruc ct = new CaTruc();
                ct.setIdCa(rs.getInt("ID_Ca"));
                ct.setTenCa(rs.getString("TenCa"));
                ct.setNgayTruc(rs.getDate("NgayTruc"));
                ct.setMaSV(rs.getString("MaSV"));
                ct.setTrangThai(rs.getString("TrangThai"));
                ct.setGhiChu(rs.getString("GhiChu"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. THÊM CA TRỰC MỚI
    public boolean insert(CaTruc ct) {
        String sql = "INSERT INTO CaTruc (TenCa, NgayTruc, MaSV, TrangThai, GhiChu) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ct.getTenCa());
            pstmt.setDate(2, new java.sql.Date(ct.getNgayTruc().getTime()));
            pstmt.setString(3, ct.getMaSV());
            pstmt.setString(4, ct.getTrangThai());
            pstmt.setString(5, ct.getGhiChu());

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. CẬP NHẬT THÔNG TIN CA TRỰC
    public boolean update(CaTruc ct) {
        String sql = "UPDATE CaTruc SET TenCa=?, NgayTruc=?, MaSV=?, TrangThai=?, GhiChu=? WHERE ID_Ca=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ct.getTenCa());
            pstmt.setDate(2, new java.sql.Date(ct.getNgayTruc().getTime()));
            pstmt.setString(3, ct.getMaSV());
            pstmt.setString(4, ct.getTrangThai());
            pstmt.setString(5, ct.getGhiChu());
            pstmt.setInt(6, ct.getIdCa());

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. XÓA CA TRỰC theo ID_Ca
    public boolean delete(int idCa) {
        String sql = "DELETE FROM CaTruc WHERE ID_Ca=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCa);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. TÌM KIẾM CA TRỰC THEO MASV HOẶC NGÀY TRỰC
    public List<CaTruc> search(String keyword) {
        List<CaTruc> list = new ArrayList<>();
        String sql = "SELECT * FROM CaTruc WHERE MaSV LIKE ? OR CONVERT(VARCHAR, NgayTruc, 120) LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CaTruc ct = new CaTruc();
                ct.setIdCa(rs.getInt("ID_Ca"));
                ct.setTenCa(rs.getString("TenCa"));
                ct.setNgayTruc(rs.getDate("NgayTruc"));
                ct.setMaSV(rs.getString("MaSV"));
                ct.setTrangThai(rs.getString("TrangThai"));
                ct.setGhiChu(rs.getString("GhiChu"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}