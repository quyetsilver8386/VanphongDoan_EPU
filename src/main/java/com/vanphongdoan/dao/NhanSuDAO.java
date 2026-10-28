package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.model.PhanQuyen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NhanSuDAO {

    // 1. LẤY TOÀN BỘ DANH SÁCH NHÂN SỰ
    public List<NhanSu> getAllNhanSu() {
        List<NhanSu> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanSu";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToNhanSu(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. LẤY DANH SÁCH NHÂN SỰ THEO BAN (Hỗ trợ Integer và int)
    public List<NhanSu> getNhanSuByBan(Integer idBan) {
        List<NhanSu> list = new ArrayList<>();
        if (idBan == null || idBan == 0) {
            return getAllNhanSu();
        }
        String sql = "SELECT * FROM NhanSu WHERE ID_Ban = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idBan);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToNhanSu(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. THÊM MỚI NHÂN SỰ
    public boolean insert(NhanSu ns) {
        String sql = "INSERT INTO NhanSu (MaSV, MatKhau, HoTen, Lop, SoDienThoai, ID_VaiTro, ID_Ban, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, ns.getMaSV());
            ps.setString(2, (ns.getMatKhau() == null || ns.getMatKhau().isEmpty()) ? "123456" : ns.getMatKhau());
            ps.setString(3, ns.getHoTen());
            ps.setString(4, ns.getLop());
            ps.setString(5, ns.getSoDienThoai());
            ps.setInt(6, ns.getIdVaiTro());

            if (ns.getIdBan() == null || ns.getIdBan() <= 0) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, ns.getIdBan());
            }

            ps.setBoolean(8, ns.isTrangThai());

            int kq = ps.executeUpdate();
            ps.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertNhanSu(NhanSu ns) {
        return insert(ns);
    }

    // 4. CẬP NHẬT NHÂN SỰ THEO MASV
    public boolean update(NhanSu ns) {
        String sql = "UPDATE NhanSu SET MatKhau = ?, HoTen = ?, Lop = ?, SoDienThoai = ?, " +
                     "ID_VaiTro = ?, ID_Ban = ?, TrangThai = ? WHERE MaSV = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, ns.getMatKhau());
            ps.setString(2, ns.getHoTen());
            ps.setString(3, ns.getLop());
            ps.setString(4, ns.getSoDienThoai());
            ps.setInt(5, ns.getIdVaiTro());

            if (ns.getIdBan() == null || ns.getIdBan() <= 0) {
                ps.setNull(6, Types.INTEGER);
            } else {
                ps.setInt(6, ns.getIdBan());
            }

            ps.setBoolean(7, ns.isTrangThai());
            ps.setString(8, ns.getMaSV());

            int kq = ps.executeUpdate();
            ps.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNhanSu(NhanSu ns) {
        return update(ns);
    }

    // 5. XÓA NHÂN SỰ THEO MASV
    public boolean delete(String maSV) {
        String sql = "DELETE FROM NhanSu WHERE MaSV = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maSV);

            int kq = ps.executeUpdate();
            ps.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. TÌM KIẾM NHÂN SỰ
    public List<NhanSu> search(String keyword) {
        List<NhanSu> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanSu WHERE MaSV LIKE ? OR HoTen LIKE ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToNhanSu(rs));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 7. KIỂM TRA ĐĂNG NHẬP (Dùng MaSV làm tài khoản)
    public NhanSu checkLogin(String username, String password) {
        String sql = "SELECT * FROM NhanSu WHERE RTRIM(LTRIM(MaSV)) = ? AND RTRIM(LTRIM(MatKhau)) = ? AND TrangThai = 1";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());

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
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, username);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PhanQuyen pq = new PhanQuyen();
                try { pq.setTenDangNhap(rs.getString(1)); } catch (Exception ignored) {}
                try { pq.setQuyenDoiNgoai(rs.getBoolean("QuyenDoiNgoai")); } catch (Exception ignored) {}
                try { pq.setQuyenSuKien(rs.getBoolean("QuyenSuKien")); } catch (Exception ignored) {}
                try { pq.setQuyenTruyenThong(rs.getBoolean("QuyenTruyenThong")); } catch (Exception ignored) {}
                try { pq.setQuyenXuatBaoCao(rs.getBoolean("QuyenXuatBaoCao")); } catch (Exception ignored) {}
                rs.close();
                ps.close();
                return pq;
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            // Trường hợp chưa dùng bảng phân quyền nâng cao
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
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
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

            int kq = ps.executeUpdate();
            ps.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 10. ĐỔI TRẠNG THÁI TÀI KHOẢN
    public boolean toggleTrangThai(String maSV, boolean trangThaiMoi) {
        String sql = "UPDATE NhanSu SET TrangThai = ? WHERE MaSV = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, trangThaiMoi);
            ps.setString(2, maSV);

            int kq = ps.executeUpdate();
            ps.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 11. RESET MẬT KHẨU
    public boolean resetMatKhau(String maSV, String matKhauMoi) {
        String sql = "UPDATE NhanSu SET MatKhau = ? WHERE MaSV = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, matKhauMoi);
            ps.setString(2, maSV);

            int kq = ps.executeUpdate();
            ps.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // HÀM CHUYỂN ĐỔI AN TOÀN TỪ RESULTSET SANG MODEL
    private NhanSu mapResultSetToNhanSu(ResultSet rs) throws Exception {
        NhanSu ns = new NhanSu();
        ns.setMaSV(rs.getString("MaSV"));
        try { ns.setTenDangNhap(rs.getString("MaSV")); } catch (Exception ignored) {}
        ns.setMatKhau(rs.getString("MatKhau"));
        ns.setHoTen(rs.getString("HoTen"));
        ns.setLop(rs.getString("Lop"));
        ns.setSoDienThoai(rs.getString("SoDienThoai"));
        ns.setIdVaiTro(rs.getInt("ID_VaiTro"));

        int idBan = rs.getInt("ID_Ban");
        ns.setIdBan(rs.wasNull() ? null : idBan);

        ns.setTrangThai(rs.getBoolean("TrangThai"));
        return ns;
    }
}