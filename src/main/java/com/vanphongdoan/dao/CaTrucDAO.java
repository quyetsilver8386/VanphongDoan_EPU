package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.CaTruc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CaTrucDAO {

    // 1. LẤY TOÀN BỘ DANH SÁCH CA TRỰC TỪ SQL SERVER
    public List<CaTruc> getAllCaTruc() {
        List<CaTruc> list = new ArrayList<>();
        String sql = "SELECT ID_CaTruc, BuoiTruc, CONVERT(VARCHAR(10), NgayTruc, 120) AS NgayTruc, "
                   + "ISNULL(Thu, '') AS Thu, ISNULL(MaSV, '') AS MaSV, ISNULL(HoTen, '') AS HoTen, "
                   + "ISNULL(TrangThai, N'Mở đăng ký') AS TrangThai, ISNULL(GhiChu, '') AS GhiChu "
                   + "FROM CaTruc ORDER BY NgayTruc DESC, BuoiTruc ASC";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CaTruc ct = new CaTruc();
                ct.setIdCa(rs.getInt("ID_CaTruc"));
                ct.setBuoiTruc(rs.getString("BuoiTruc"));
                ct.setNgayTruc(rs.getString("NgayTruc"));
                ct.setThu(rs.getString("Thu"));
                ct.setMaSV(rs.getString("MaSV"));
                ct.setHoTen(rs.getString("HoTen"));
                ct.setTrangThai(rs.getString("TrangThai"));
                ct.setGhiChu(rs.getString("GhiChu"));
                list.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. KHỞI TẠO TỰ ĐỘNG LỊCH TRỰC TUẦN (Thứ 2 -> Thứ 6, mỗi ngày 2 ca Sáng & Chiều)
    public boolean taoLichTrucTuan(LocalDate ngayThuHai) {
        String sql = "INSERT INTO CaTruc (BuoiTruc, NgayTruc, Thu, TrangThai, GhiChu, SoLuongToiDa) "
                   + "VALUES (?, ?, ?, N'Mở đăng ký', N'Ca trực văn phòng', 4)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        String[] dsThu = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6"};
        try {
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < 5; i++) {
                    LocalDate ngay = ngayThuHai.plusDays(i);
                    String strNgay = ngay.toString(); // YYYY-MM-DD
                    String thu = dsThu[i];

                    // Ca Sáng
                    pstmt.setString(1, "Sáng");
                    pstmt.setString(2, strNgay);
                    pstmt.setString(3, thu);
                    pstmt.addBatch();

                    // Ca Chiều
                    pstmt.setString(1, "Chiều");
                    pstmt.setString(2, strNgay);
                    pstmt.setString(3, thu);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
                conn.setAutoCommit(autoCommit);
                return true;
            } catch (Exception ex) {
                conn.rollback();
                conn.setAutoCommit(autoCommit);
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. THÀNH VIÊN ĐĂNG KÝ CA TRỰC (Chỉ đăng ký được ca đang 'Mở đăng ký')
    public boolean dangKyCaTruc(int idCa, String maSV, String hoTen) {
        String sql = "UPDATE CaTruc SET MaSV = ?, HoTen = ?, TrangThai = N'Đã phân công' "
                   + "WHERE ID_CaTruc = ? AND TrangThai = N'Mở đăng ký'";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maSV);
            pstmt.setString(2, hoTen);
            pstmt.setInt(3, idCa);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. THÀNH VIÊN HỦY ĐĂNG KÝ CA TRỰC (Chỉ hủy được ca của chính mình)
    public boolean huyDangKyCaTruc(int idCa, String maSV) {
        String sql = "UPDATE CaTruc SET MaSV = NULL, HoTen = NULL, TrangThai = N'Mở đăng ký' "
                   + "WHERE ID_CaTruc = ? AND MaSV = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCa);
            pstmt.setString(2, maSV);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. ADMIN THÊM CA TRỰC ĐƠN LẺ
    public boolean insert(CaTruc ct) {
        String sql = "INSERT INTO CaTruc (BuoiTruc, NgayTruc, Thu, MaSV, HoTen, TrangThai, GhiChu, SoLuongToiDa) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, 4)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ct.getBuoiTruc());
            pstmt.setString(2, ct.getNgayTruc());
            pstmt.setString(3, ct.getThu());
            pstmt.setString(4, ct.getMaSV());
            pstmt.setString(5, ct.getHoTen());
            pstmt.setString(6, ct.getTrangThai());
            pstmt.setString(7, ct.getGhiChu());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. CẬP NHẬT THÔNG TIN CA TRỰC
    public boolean update(CaTruc ct) {
        String sql = "UPDATE CaTruc SET BuoiTruc = ?, NgayTruc = ?, Thu = ?, MaSV = ?, HoTen = ?, TrangThai = ?, GhiChu = ? "
                   + "WHERE ID_CaTruc = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ct.getBuoiTruc());
            pstmt.setString(2, ct.getNgayTruc());
            pstmt.setString(3, ct.getThu());
            pstmt.setString(4, ct.getMaSV());
            pstmt.setString(5, ct.getHoTen());
            pstmt.setString(6, ct.getTrangThai());
            pstmt.setString(7, ct.getGhiChu());
            pstmt.setInt(8, ct.getIdCa());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 7. XÓA CA TRỰC THEO ID_CaTruc
    public boolean delete(int idCa) {
        String sql = "DELETE FROM CaTruc WHERE ID_CaTruc = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCa);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 8. TÌM KIẾM THEO MÃ SV, HỌ TÊN HOẶC NGÀY TRỰC
    public List<CaTruc> search(String keyword) {
        List<CaTruc> list = new ArrayList<>();
        String sql = "SELECT ID_CaTruc, BuoiTruc, CONVERT(VARCHAR(10), NgayTruc, 120) AS NgayTruc, "
                   + "ISNULL(Thu, '') AS Thu, ISNULL(MaSV, '') AS MaSV, ISNULL(HoTen, '') AS HoTen, "
                   + "ISNULL(TrangThai, N'Mở đăng ký') AS TrangThai, ISNULL(GhiChu, '') AS GhiChu "
                   + "FROM CaTruc "
                   + "WHERE MaSV LIKE ? OR HoTen LIKE ? OR CONVERT(VARCHAR(10), NgayTruc, 120) LIKE ? "
                   + "ORDER BY NgayTruc DESC, BuoiTruc ASC";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return list;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            pstmt.setString(1, kw);
            pstmt.setString(2, kw);
            pstmt.setString(3, kw);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CaTruc ct = new CaTruc();
                    ct.setIdCa(rs.getInt("ID_CaTruc"));
                    ct.setBuoiTruc(rs.getString("BuoiTruc"));
                    ct.setNgayTruc(rs.getString("NgayTruc"));
                    ct.setThu(rs.getString("Thu"));
                    ct.setMaSV(rs.getString("MaSV"));
                    ct.setHoTen(rs.getString("HoTen"));
                    ct.setTrangThai(rs.getString("TrangThai"));
                    ct.setGhiChu(rs.getString("GhiChu"));
                    list.add(ct);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean khoiTaoLichTuan(java.time.LocalDate mondayOfWeek) {
        String checkSql = "SELECT COUNT(*) FROM CaTruc WHERE NgayTruc = ? AND BuoiTruc = ?";
        String insertSql = "INSERT INTO CaTruc (NgayTruc, BuoiTruc, SoLuongToiDa, Thu, TrangThai) VALUES (?, ?, 1, ?, N'Mở đăng ký')";
        
        try (java.sql.Connection conn = com.vanphongdoan.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement psCheck = conn.prepareStatement(checkSql);
             java.sql.PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
            
            for (int i = 0; i < 5; i++) { // Thứ 2 (i=0) đến Thứ 6 (i=4)
                java.time.LocalDate date = mondayOfWeek.plusDays(i);
                java.sql.Date sqlDate = java.sql.Date.valueOf(date);
                String thu = "Thứ " + (i + 2);

                // 1. Ca Sáng
                psCheck.setDate(1, sqlDate);
                psCheck.setString(2, "Sáng");
                try (java.sql.ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        psInsert.setDate(1, sqlDate);
                        psInsert.setString(2, "Sáng");
                        psInsert.setString(3, thu);
                        psInsert.addBatch();
                    }
                }

                // 2. Ca Chiều
                psCheck.setDate(1, sqlDate);
                psCheck.setString(2, "Chiều");
                try (java.sql.ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        psInsert.setDate(1, sqlDate);
                        psInsert.setString(2, "Chiều");
                        psInsert.setString(3, thu);
                        psInsert.addBatch();
                    }
                }
            }
            psInsert.executeBatch();
            return true;
        } catch (Exception e) {
            System.err.println(">>> LỖI KHỞI TẠO LỊCH TUẦN: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}