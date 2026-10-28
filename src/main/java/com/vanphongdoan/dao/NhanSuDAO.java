package com.vanphongdoan.dao;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.NhanSu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NhanSuDAO {

    // 1. Lấy toàn bộ danh sách nhân sự (JOIN với bảng VaiTro và BanChuyenMon)
    public List<NhanSu> getAllNhanSu() {
        List<NhanSu> list = new ArrayList<>();
        String sql = "SELECT ns.*, vt.TenVaiTro, bcm.TenBan " +
                     "FROM NhanSu ns " +
                     "LEFT JOIN VaiTro vt ON ns.ID_VaiTro = vt.ID_VaiTro " +
                     "LEFT JOIN BanChuyenMon bcm ON ns.ID_Ban = bcm.ID_Ban";

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                NhanSu ns = new NhanSu();
                ns.setTenDangNhap(rs.getString("TenDangNhap"));
                ns.setMatKhau(rs.getString("MatKhau"));
                ns.setMaSV(rs.getString("MaSV"));
                ns.setHoTen(rs.getString("HoTen"));
                ns.setKhoa(rs.getString("Khoa"));
                ns.setLop(rs.getString("Lop"));
                ns.setSoDienThoai(rs.getString("SoDienThoai"));
                ns.setIdVaiTro(rs.getInt("ID_VaiTro"));
                
                String tenVT = rs.getString("TenVaiTro");
                ns.setTenVaiTro(tenVT != null ? tenVT : "Chưa phân quyền");

                int idBan = rs.getInt("ID_Ban");
                if (rs.wasNull()) {
                    ns.setIdBan(null);
                    ns.setTenBan("Không thuộc ban");
                } else {
                    ns.setIdBan(idBan);
                    ns.setTenBan(rs.getString("TenBan"));
                }

                ns.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(ns);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm mới tài khoản (Mật khẩu mặc định là 123456)
    public boolean insertNhanSu(NhanSu ns) {
        String sql = "INSERT INTO NhanSu (TenDangNhap, MatKhau, MaSV, HoTen, Khoa, Lop, SoDienThoai, ID_VaiTro, ID_Ban, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, ns.getTenDangNhap());
            ps.setString(2, "123456"); // Mật khẩu khởi tạo
            ps.setString(3, ns.getMaSV());
            ps.setString(4, ns.getHoTen());
            ps.setString(5, ns.getKhoa());
            ps.setString(6, ns.getLop());
            ps.setString(7, ns.getSoDienThoai());
            ps.setInt(8, ns.getIdVaiTro());

            if (ns.getIdBan() == null || ns.getIdBan() <= 0) {
                ps.setNull(9, Types.INTEGER);
            } else {
                ps.setInt(9, ns.getIdBan());
            }

            ps.setBoolean(10, ns.isTrangThai());

            int kq = ps.executeUpdate();
            ps.close();
            conn.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Cập nhật hồ sơ và vai trò theo TenDangNhap
    public boolean updateNhanSu(NhanSu ns) {
        String sql = "UPDATE NhanSu SET MaSV = ?, HoTen = ?, Khoa = ?, Lop = ?, SoDienThoai = ?, " +
                     "ID_VaiTro = ?, ID_Ban = ?, TrangThai = ? WHERE TenDangNhap = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, ns.getMaSV());
            ps.setString(2, ns.getHoTen());
            ps.setString(3, ns.getKhoa());
            ps.setString(4, ns.getLop());
            ps.setString(5, ns.getSoDienThoai());
            ps.setInt(6, ns.getIdVaiTro());

            if (ns.getIdBan() == null || ns.getIdBan() <= 0) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, ns.getIdBan());
            }

            ps.setBoolean(8, ns.isTrangThai());
            ps.setString(9, ns.getTenDangNhap());

            int kq = ps.executeUpdate();
            ps.close();
            conn.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Khóa hoặc Mở khóa tài khoản
    public boolean toggleTrangThai(String tenDangNhap, boolean trangThaiMoi) {
        String sql = "UPDATE NhanSu SET TrangThai = ? WHERE TenDangNhap = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, trangThaiMoi);
            ps.setString(2, tenDangNhap);

            int kq = ps.executeUpdate();
            ps.close();
            conn.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Reset mật khẩu về mặc định
    public boolean resetMatKhau(String tenDangNhap, String matKhauMoi) {
        String sql = "UPDATE NhanSu SET MatKhau = ? WHERE TenDangNhap = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, matKhauMoi);
            ps.setString(2, tenDangNhap);

            int kq = ps.executeUpdate();
            ps.close();
            conn.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 6. Kiểm tra đăng nhập theo Tên đăng nhập và Mật khẩu
    public NhanSu checkLogin(String username, String password) {
        String sql = "SELECT * FROM NhanSu WHERE RTRIM(LTRIM(TenDangNhap)) = ? AND RTRIM(LTRIM(MatKhau)) = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhanSu ns = new NhanSu();
                    ns.setMaSV(rs.getString("MaSV"));
                    ns.setHoTen(rs.getString("HoTen"));
                    ns.setTenDangNhap(rs.getString("TenDangNhap"));
                    
                    // ĐỌC TRẠNG THÁI HOẠT ĐỘNG (1 = true = Hoạt động, 0 = false = Khóa)
                    try {
                        ns.setTrangThai(rs.getBoolean("TrangThai"));
                    } catch (Exception ex) {
                        ns.setTrangThai(true); // Mặc định mở nếu không đọc được cột
                    }
                    
                    try {
                        ns.setIdVaiTro(rs.getInt("ID_VaiTro"));
                    } catch (Exception ignored) {}
                    
                    return ns;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 7. Lấy thông tin phân quyền chi tiết của 1 tài khoản BĐH
    public com.vanphongdoan.model.PhanQuyen getPhanQuyenByUsername(String tenDangNhap) {
        String sql = "SELECT * FROM PhanQuyenBanDieuHanh WHERE TenDangNhap = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tenDangNhap);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                com.vanphongdoan.model.PhanQuyen pq = new com.vanphongdoan.model.PhanQuyen();
                pq.setTenDangNhap(rs.getString("TenDangNhap"));
                pq.setQuyenDoiNgoai(rs.getBoolean("QuyenDoiNgoai"));
                pq.setQuyenSuKien(rs.getBoolean("QuyenSuKien"));
                pq.setQuyenTruyenThong(rs.getBoolean("QuyenTruyenThong"));
                pq.setQuyenXuatBaoCao(rs.getBoolean("QuyenXuatBaoCao"));
                rs.close(); ps.close(); conn.close();
                return pq;
            }
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 8. Cập nhật hoặc cấp mới phân quyền chi tiết
    public boolean savePhanQuyen(com.vanphongdoan.model.PhanQuyen pq) {
        String sql = "IF EXISTS (SELECT 1 FROM PhanQuyenBanDieuHanh WHERE TenDangNhap = ?) " +
                     "    UPDATE PhanQuyenBanDieuHanh SET QuyenDoiNgoai = ?, QuyenSuKien = ?, QuyenTruyenThong = ?, QuyenXuatBaoCao = ? WHERE TenDangNhap = ? " +
                     "ELSE " +
                     "    INSERT INTO PhanQuyenBanDieuHanh (TenDangNhap, QuyenDoiNgoai, QuyenSuKien, QuyenTruyenThong, QuyenXuatBaoCao) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            // Dành cho UPDATE
            ps.setString(1, pq.getTenDangNhap());
            ps.setBoolean(2, pq.isQuyenDoiNgoai());
            ps.setBoolean(3, pq.isQuyenSuKien());
            ps.setBoolean(4, pq.isQuyenTruyenThong());
            ps.setBoolean(5, pq.isQuyenXuatBaoCao());
            ps.setString(6, pq.getTenDangNhap());

            // Dành cho INSERT (nếu chưa có)
            ps.setString(7, pq.getTenDangNhap());
            ps.setBoolean(8, pq.isQuyenDoiNgoai());
            ps.setBoolean(9, pq.isQuyenSuKien());
            ps.setBoolean(10, pq.isQuyenTruyenThong());
            ps.setBoolean(11, pq.isQuyenXuatBaoCao());

            int kq = ps.executeUpdate();
            ps.close(); conn.close();
            return kq > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 9. Lấy danh sách nhân sự theo ID Ban cụ thể (Dành cho Ban Điều hành)
    public List<NhanSu> getNhanSuByBan(int idBan) {
        List<NhanSu> list = new ArrayList<>();
        String sql = "SELECT ns.*, vt.TenVaiTro, b.TenBan " +
                     "FROM NhanSu ns " +
                     "LEFT JOIN VaiTro vt ON ns.ID_VaiTro = vt.ID_VaiTro " +
                     "LEFT JOIN Ban b ON ns.ID_Ban = b.ID_Ban " +
                     "WHERE ns.ID_Ban = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBan);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NhanSu ns = new NhanSu();
                ns.setTenDangNhap(rs.getString("TenDangNhap"));
                ns.setMaSV(rs.getString("MaSV"));
                ns.setHoTen(rs.getString("HoTen"));
                ns.setKhoa(rs.getString("Khoa"));
                ns.setLop(rs.getString("Lop"));
                ns.setSoDienThoai(rs.getString("SoDienThoai"));
                ns.setIdVaiTro(rs.getInt("ID_VaiTro"));
                ns.setIdBan(rs.getInt("ID_Ban"));
                ns.setTenVaiTro(rs.getString("TenVaiTro"));
                ns.setTenBan(rs.getString("TenBan"));
                ns.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(ns);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
}