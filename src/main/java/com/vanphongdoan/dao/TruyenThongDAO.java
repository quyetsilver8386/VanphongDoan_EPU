package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TruyenThongDAO {

    public List<Object[]> getAll() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT nv.ID_NhiemVu, nv.TenNhiemVu, " +
                     "ISNULL(ns.HoTen + ' - ' + nv.MaSV_DuocGiao, nv.MaSV_DuocGiao) AS NguoiPhuTrach, " +
                     "nv.HanChot, nv.TrangThai, ISNULL(nv.MoTa, '') AS MoTa " +
                     "FROM NhiemVuTruyenThong nv " +
                     "LEFT JOIN NhanSu ns ON LTRIM(RTRIM(nv.MaSV_DuocGiao)) = LTRIM(RTRIM(ns.MaSV)) " +
                     "ORDER BY nv.ID_NhiemVu DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    String.valueOf(rs.getInt("ID_NhiemVu")),
                    rs.getString("TenNhiemVu"),
                    rs.getString("NguoiPhuTrach") != null ? rs.getString("NguoiPhuTrach") : "",
                    rs.getDate("HanChot") != null ? rs.getDate("HanChot").toString() : "",
                    rs.getString("TrangThai"),
                    rs.getString("MoTa")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String extractMaSV(String nguoiPhuTrach) {
        if (nguoiPhuTrach == null) return "";
        if (nguoiPhuTrach.contains("-")) {
            return nguoiPhuTrach.substring(nguoiPhuTrach.lastIndexOf("-") + 1).trim();
        }
        return nguoiPhuTrach.trim();
    }

    public boolean insert(String tenNhiemVu, String nguoiPhuTrach, String hanChot, String trangThai, String moTa) {
        String sql = "INSERT INTO NhiemVuTruyenThong (TenNhiemVu, MaSV_DuocGiao, HanChot, TrangThai, MoTa) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, tenNhiemVu);
            ps.setString(2, extractMaSV(nguoiPhuTrach));
            if (hanChot == null || hanChot.trim().isEmpty()) ps.setNull(3, Types.DATE);
            else ps.setString(3, hanChot.trim());
            ps.setNString(4, trangThai);
            ps.setNString(5, moTa != null ? moTa : "");
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int idNhiemVu, String tenNhiemVu, String nguoiPhuTrach, String hanChot, String trangThai, String moTa) {
        String sql = "UPDATE NhiemVuTruyenThong SET TenNhiemVu = ?, MaSV_DuocGiao = ?, HanChot = ?, TrangThai = ?, MoTa = ? WHERE ID_NhiemVu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, tenNhiemVu);
            ps.setString(2, extractMaSV(nguoiPhuTrach));
            if (hanChot == null || hanChot.trim().isEmpty()) ps.setNull(3, Types.DATE);
            else ps.setString(3, hanChot.trim());
            ps.setNString(4, trangThai);
            ps.setNString(5, moTa != null ? moTa : "");
            ps.setInt(6, idNhiemVu);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Lấy danh sách nhiệm vụ của đúng thành viên (tự động khớp MaSV, TenDangNhap hoặc HoTen)
    public List<Object[]> getByMember(String maSV, String tenDangNhap, String hoTen) {
        List<Object[]> list = new ArrayList<>();
        String m = (maSV != null) ? maSV.trim() : "";
        String u = (tenDangNhap != null) ? tenDangNhap.trim() : "";
        String h = (hoTen != null) ? hoTen.trim() : "";

        String sql = "SELECT nv.ID_NhiemVu, nv.TenNhiemVu, nv.HanChot, nv.TrangThai, ISNULL(nv.MoTa, '') AS MoTa " +
                     "FROM NhiemVuTruyenThong nv " +
                     "LEFT JOIN NhanSu ns ON LTRIM(RTRIM(nv.MaSV_DuocGiao)) = LTRIM(RTRIM(ns.MaSV)) " +
                     "WHERE (LTRIM(RTRIM(nv.MaSV_DuocGiao)) = ? AND ? <> '') " +
                     "   OR (LTRIM(RTRIM(ns.TenDangNhap)) = ? AND ? <> '') " +
                     "   OR (LTRIM(RTRIM(nv.MaSV_DuocGiao)) = ? AND ? <> '') " +
                     "   OR (nv.MaSV_DuocGiao LIKE ? AND ? <> '') " +
                     "ORDER BY nv.ID_NhiemVu DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m); ps.setString(2, m);
            ps.setString(3, u); ps.setString(4, u);
            ps.setString(5, u); ps.setString(6, u);
            ps.setNString(7, "%" + h + "%"); ps.setNString(8, h);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("ID_NhiemVu"),
                        rs.getString("TenNhiemVu"),
                        rs.getDate("HanChot") != null ? rs.getDate("HanChot").toString() : "Không thời hạn",
                        rs.getString("TrangThai"),
                        rs.getString("MoTa")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thành viên cập nhật nhanh trạng thái tiến độ
    public boolean updateStatus(int idNhiemVu, String trangThai) {
        String sql = "UPDATE NhiemVuTruyenThong SET TrangThai = ? WHERE ID_NhiemVu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setNString(1, trangThai);
            ps.setInt(2, idNhiemVu);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int idNhiemVu) {
        String sql = "DELETE FROM NhiemVuTruyenThong WHERE ID_NhiemVu = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idNhiemVu);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}