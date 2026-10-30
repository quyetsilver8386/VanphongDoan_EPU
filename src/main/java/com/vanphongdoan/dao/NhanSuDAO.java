package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.model.PhanQuyen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NhanSuDAO {

    /**
     * 1. Kiểm tra đăng nhập tài khoản
     */
    public NhanSu checkLogin(String tenDangNhap, String matKhau) {
        String sql = "SELECT ns.*, b.TenBan FROM NhanSu ns "
                   + "LEFT JOIN BanChuyenMon b ON ns.ID_Ban = b.ID_Ban "
                   + "WHERE ns.MaSV = ? AND ns.MatKhau = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapNhanSuFromResultSet(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 2. Đổi mật khẩu cá nhân
     */
    public boolean doiMatKhau(String maSV, String matKhauCu, String matKhauMoi) {
        String sql = "UPDATE NhanSu SET MatKhau = ? WHERE MaSV = ? AND MatKhau = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matKhauMoi);
            ps.setString(2, maSV);
            ps.setString(3, matKhauCu);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 3. Lấy danh sách toàn bộ nhân sự
     */
    public List<NhanSu> getAllNhanSu() {
        List<NhanSu> list = new ArrayList<>();
        String sql = "SELECT ns.*, b.TenBan FROM NhanSu ns "
                   + "LEFT JOIN BanChuyenMon b ON ns.ID_Ban = b.ID_Ban";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapNhanSuFromResultSet(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 4. Lấy danh sách nhân sự lọc theo Ban Chuyên Môn
     */
    public List<NhanSu> getNhanSuByBan(Integer idBan) {
        if (idBan == null || idBan <= 0) {
            return getAllNhanSu();
        }
        List<NhanSu> list = new ArrayList<>();
        String sql = "SELECT ns.*, b.TenBan FROM NhanSu ns "
                   + "LEFT JOIN BanChuyenMon b ON ns.ID_Ban = b.ID_Ban WHERE ns.ID_Ban = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idBan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapNhanSuFromResultSet(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 5. Thêm mới nhân sự (Admin)
     */
    public boolean insertNhanSu(NhanSu ns) {
        String sql = "INSERT INTO NhanSu (MaSV, MatKhau, HoTen, Lop, SoDienThoai, TrangThai, ID_VaiTro, ID_Ban) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ns.getMaSV());
            ps.setString(2, (ns.getMatKhau() != null && !ns.getMatKhau().isEmpty()) ? ns.getMatKhau() : "123456");
            ps.setString(3, ns.getHoTen());
            ps.setString(4, ns.getLop());
            ps.setString(5, ns.getSoDienThoai());
            ps.setBoolean(6, ns.isTrangThai());
            ps.setInt(7, ns.getIdVaiTro());

            if (ns.getIdBan() > 0) {
                ps.setInt(8, ns.getIdBan());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 6. Cập nhật thông tin nhân sự (Admin)
     */
    public boolean updateNhanSu(NhanSu ns) {
        String sql = "UPDATE NhanSu SET HoTen = ?, Lop = ?, SoDienThoai = ?, ID_VaiTro = ?, ID_Ban = ? WHERE MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ns.getHoTen());
            ps.setString(2, ns.getLop());
            ps.setString(3, ns.getSoDienThoai());
            ps.setInt(4, ns.getIdVaiTro());

            if (ns.getIdBan() > 0) {
                ps.setInt(5, ns.getIdBan());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.setString(6, ns.getMaSV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 7. Khóa / Mở khóa tài khoản nhân sự
     */
    public boolean toggleTrangThai(String maSV, boolean status) {
        String sql = "UPDATE NhanSu SET TrangThai = ? WHERE MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, status);
            ps.setString(2, maSV);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 8. Đặt lại mật khẩu về mặc định
     */
    public boolean resetMatKhau(String maSV, String newPass) {
        String sql = "UPDATE NhanSu SET MatKhau = ? WHERE MaSV = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, (newPass != null && !newPass.isEmpty()) ? newPass : "123456");
            ps.setString(2, maSV);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 9. Lấy thông tin phân quyền Ban Điều Hành
     */
    public PhanQuyen getPhanQuyenByUsername(String username) {
        String sql = "SELECT * FROM PhanQuyenBanDieuHanh WHERE TenDangNhap = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PhanQuyen pq = new PhanQuyen();
                    pq.setTenDangNhap(rs.getString("TenDangNhap"));
                    pq.setQuyenDoiNgoai(rs.getBoolean("QuyenDoiNgoai"));
                    pq.setQuyenSuKien(rs.getBoolean("QuyenSuKien"));
                    pq.setQuyenTruyenThong(rs.getBoolean("QuyenTruyenThong"));
                    pq.setQuyenXuatBaoCao(rs.getBoolean("QuyenXuatBaoCao"));
                    return pq;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Trả về đối tượng phân quyền mặc định nếu chưa có
        return new PhanQuyen(username, false, false, false, false);
    }

    /**
     * 10. Lưu / Cập nhật bảng phân quyền Ban Điều Hành
     */
    public boolean savePhanQuyen(PhanQuyen pq) {
        String checkSql = "SELECT COUNT(*) FROM PhanQuyenBanDieuHanh WHERE TenDangNhap = ?";
        String insertSql = "INSERT INTO PhanQuyenBanDieuHanh (TenDangNhap, QuyenDoiNgoai, QuyenSuKien, QuyenTruyenThong, QuyenXuatBaoCao) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE PhanQuyenBanDieuHanh SET QuyenDoiNgoai = ?, QuyenSuKien = ?, QuyenTruyenThong = ?, QuyenXuatBaoCao = ? WHERE TenDangNhap = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, pq.getTenDangNhap());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
            }

            if (exists) {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setBoolean(1, pq.isQuyenDoiNgoai());
                    ps.setBoolean(2, pq.isQuyenSuKien());
                    ps.setBoolean(3, pq.isQuyenTruyenThong());
                    ps.setBoolean(4, pq.isQuyenXuatBaoCao());
                    ps.setString(5, pq.getTenDangNhap());
                    return ps.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, pq.getTenDangNhap());
                    ps.setBoolean(2, pq.isQuyenDoiNgoai());
                    ps.setBoolean(3, pq.isQuyenSuKien());
                    ps.setBoolean(4, pq.isQuyenTruyenThong());
                    ps.setBoolean(5, pq.isQuyenXuatBaoCao());
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper map ResultSet sang Object NhanSu
     */
    private NhanSu mapNhanSuFromResultSet(ResultSet rs) throws Exception {
        NhanSu ns = new NhanSu();
        ns.setMaSV(rs.getString("MaSV"));
        ns.setMatKhau(rs.getString("MatKhau"));
        ns.setHoTen(rs.getString("HoTen"));
        ns.setLop(rs.getString("Lop"));
        ns.setSoDienThoai(rs.getString("SoDienThoai"));
        ns.setTrangThai(rs.getBoolean("TrangThai"));
        ns.setIdVaiTro(rs.getInt("ID_VaiTro"));
        ns.setIdBan(rs.getInt("ID_Ban"));
        try {
            ns.setTenBan(rs.getString("TenBan"));
        } catch (Exception ignored) {}
        return ns;
    }
}