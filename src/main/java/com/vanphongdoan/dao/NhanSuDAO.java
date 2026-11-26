package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.model.PhanQuyen;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanSuDAO {

    // 1. LẤY TOÀN BỘ DANH SÁCH NHÂN SỰ
    public List<NhanSu> getAllNhanSu() {
        List<NhanSu> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanSu";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToNhanSu(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. LẤY DANH SÁCH NHÂN SỰ THEO BAN
    public List<NhanSu> getNhanSuByBan(Integer idBan) {
        List<NhanSu> list = new ArrayList<>();
        if (idBan == null || idBan == 0) {
            return getAllNhanSu();
        }
        String sql = "SELECT * FROM NhanSu WHERE ID_Ban = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanSu(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. THÊM MỚI NHÂN SỰ (Kiểm tra trùng & thông báo tiếng Việt rõ ràng)
    public boolean insertNhanSu(NhanSu ns) {
        String sql = "INSERT INTO NhanSu (MaSV, MatKhau, HoTen, Khoa, Lop, SoDienThoai, TrangThai, ID_VaiTro, ID_Ban, TenDangNhap, VaiTro) "
                   + "VALUES (?, '123456', ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ns.getMaSV());
            ps.setString(2, ns.getHoTen());
            ps.setString(3, ns.getKhoa());
            ps.setString(4, ns.getLop());
            ps.setString(5, ns.getSoDienThoai());
            ps.setBoolean(6, ns.isTrangThai());
            ps.setInt(7, ns.getIdVaiTro());

            if (ns.getIdBan() != null && ns.getIdBan() > 0) {
                ps.setInt(8, ns.getIdBan());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setString(9, ns.getTenDangNhap());

            String tenVaiTro = "Thành viên";
            if (ns.getIdVaiTro() == 1) tenVaiTro = "Admin";
            else if (ns.getIdVaiTro() == 2) tenVaiTro = "Ban Điều hành";
            ps.setString(10, tenVaiTro);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("UQ_NhanSu_TenDangNhap")) {
                JOptionPane.showMessageDialog(null,
                        "Tên đăng nhập '" + ns.getTenDangNhap() + "' đã tồn tại! Vui lòng nhập tên khác.",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
            } else if (msg != null && msg.contains("PRIMARY KEY")) {
                JOptionPane.showMessageDialog(null,
                        "Mã SV / Cán bộ '" + ns.getMaSV() + "' đã tồn tại trong hệ thống!",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Lỗi CSDL khi thêm:\n" + msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. CẬP NHẬT NHÂN SỰ THEO MASV
    public boolean updateNhanSu(NhanSu ns) {
        String sql = "UPDATE NhanSu SET HoTen = ?, Khoa = ?, Lop = ?, SoDienThoai = ?, "
                   + "ID_VaiTro = ?, ID_Ban = ?, TrangThai = ?, VaiTro = ? WHERE MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ns.getHoTen());
            ps.setString(2, ns.getKhoa());
            ps.setString(3, ns.getLop());
            ps.setString(4, ns.getSoDienThoai());
            ps.setInt(5, ns.getIdVaiTro());

            if (ns.getIdBan() != null && ns.getIdBan() > 0) {
                ps.setInt(6, ns.getIdBan());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setBoolean(7, ns.isTrangThai());

            String tenVaiTro = "Thành viên";
            if (ns.getIdVaiTro() == 1) tenVaiTro = "Admin";
            else if (ns.getIdVaiTro() == 2) tenVaiTro = "Ban Điều hành";
            ps.setString(8, tenVaiTro);

            ps.setString(9, ns.getMaSV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Chi tiết lỗi SQL:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    // 5. XÓA NHÂN SỰ THEO MASV
    public boolean delete(String maSV) {
        String sql = "DELETE FROM NhanSu WHERE MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. TÌM KIẾM NHÂN SỰ
    public List<NhanSu> search(String keyword) {
        List<NhanSu> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanSu WHERE TenDangNhap LIKE ? OR MaSV LIKE ? OR HoTen LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanSu(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 7. KIỂM TRA ĐĂNG NHẬP (Hỗ trợ đăng nhập bằng cả TenDangNhap lẫn MaSV)
    public NhanSu checkLogin(String username, String password) {
        String sql = "SELECT * FROM NhanSu WHERE (RTRIM(LTRIM(TenDangNhap)) = ? OR RTRIM(LTRIM(MaSV)) = ?) "
                   + "AND RTRIM(LTRIM(MatKhau)) = ? AND TrangThai = 1";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, username.trim());
            ps.setString(3, password.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanSu(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 8. LẤY THÔNG TIN PHÂN QUYỀN
    public PhanQuyen getPhanQuyenByUsername(String username) {
        String sql = "SELECT * FROM PhanQuyenBanDieuHanh WHERE TenDangNhap = ? OR MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PhanQuyen pq = new PhanQuyen();
                    try { pq.setTenDangNhap(rs.getString(1)); } catch (Exception ignored) {}
                    try { pq.setQuyenDoiNgoai(rs.getBoolean("QuyenDoiNgoai")); } catch (Exception ignored) {}
                    try { pq.setQuyenSuKien(rs.getBoolean("QuyenSuKien")); } catch (Exception ignored) {}
                    try { pq.setQuyenTruyenThong(rs.getBoolean("QuyenTruyenThong")); } catch (Exception ignored) {}
                    try { pq.setQuyenXuatBaoCao(rs.getBoolean("QuyenXuatBaoCao")); } catch (Exception ignored) {}
                    return pq;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    // 9. LƯU THÔNG TIN PHÂN QUYỀN
    public boolean savePhanQuyen(PhanQuyen pq) {
        if (pq == null) return false;
        String sql = "IF EXISTS (SELECT 1 FROM PhanQuyenBanDieuHanh WHERE TenDangNhap = ?) " +
                     "    UPDATE PhanQuyenBanDieuHanh SET QuyenDoiNgoai = ?, QuyenSuKien = ?, QuyenTruyenThong = ?, QuyenXuatBaoCao = ? WHERE TenDangNhap = ? " +
                     "ELSE " +
                     "    INSERT INTO PhanQuyenBanDieuHanh (TenDangNhap, QuyenDoiNgoai, QuyenSuKien, QuyenTruyenThong, QuyenXuatBaoCao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String user = pq.getTenDangNhap();
            ps.setString(1, user);
            ps.setBoolean(2, pq.isQuyenDoiNgoai());
            ps.setBoolean(3, pq.isQuyenSuKien());
            ps.setBoolean(4, pq.isQuyenTruyenThong());
            ps.setBoolean(5, pq.isQuyenXuatBaoCao());
            ps.setString(6, user);

            ps.setString(7, user);
            ps.setBoolean(8, pq.isQuyenDoiNgoai());
            ps.setBoolean(9, pq.isQuyenSuKien());
            ps.setBoolean(10, pq.isQuyenTruyenThong());
            ps.setBoolean(11, pq.isQuyenXuatBaoCao());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 10. ĐỔI TRẠNG THÁI TÀI KHOẢN (Khớp cả TenDangNhap và MaSV)
    public boolean toggleTrangThai(String tenDangNhapOrMaSV, boolean trangThaiMoi) {
        String sql = "UPDATE NhanSu SET TrangThai = ? WHERE TenDangNhap = ? OR MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, trangThaiMoi);
            ps.setString(2, tenDangNhapOrMaSV);
            ps.setString(3, tenDangNhapOrMaSV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 11. RESET MẬT KHẨU (Khớp cả TenDangNhap và MaSV)
    public boolean resetMatKhau(String tenDangNhapOrMaSV, String matKhauMoi) {
        String sql = "UPDATE NhanSu SET MatKhau = ? WHERE TenDangNhap = ? OR MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matKhauMoi);
            ps.setString(2, tenDangNhapOrMaSV);
            ps.setString(3, tenDangNhapOrMaSV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // HÀM CHUYỂN ĐỔI AN TOÀN TỪ RESULTSET SANG MODEL (Đọc đầy đủ TenDangNhap và Khoa)
    private NhanSu mapResultSetToNhanSu(ResultSet rs) throws Exception {
        NhanSu ns = new NhanSu();
        ns.setMaSV(rs.getString("MaSV"));

        String tenDN = rs.getString("TenDangNhap");
        ns.setTenDangNhap((tenDN != null && !tenDN.trim().isEmpty()) ? tenDN.trim() : rs.getString("MaSV"));

        ns.setMatKhau(rs.getString("MatKhau"));
        ns.setHoTen(rs.getString("HoTen"));
        try { ns.setKhoa(rs.getString("Khoa")); } catch (Exception ignored) {}
        ns.setLop(rs.getString("Lop"));
        ns.setSoDienThoai(rs.getString("SoDienThoai"));
        ns.setIdVaiTro(rs.getInt("ID_VaiTro"));
        try { ns.setTenVaiTro(rs.getString("VaiTro")); } catch (Exception ignored) {}

        int idBan = rs.getInt("ID_Ban");
        ns.setIdBan(rs.wasNull() ? null : idBan);

        ns.setTrangThai(rs.getBoolean("TrangThai"));
        return ns;
    }

    // 12. ĐỔI MẬT KHẨU CHO THÀNH VIÊN
    public boolean doiMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        String sql = "UPDATE NhanSu SET MatKhau = ? WHERE (TenDangNhap = ? OR MaSV = ?) AND MatKhau = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matKhauMoi);
            pstmt.setString(2, tenDangNhap);
            pstmt.setString(3, tenDangNhap);
            pstmt.setString(4, matKhauCu);

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}