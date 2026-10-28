package com.vanphongdoan.model;

public class PhanQuyen {
    private String tenDangNhap;
    private boolean quyenDoiNgoai;    // Quản lý ca trực & sổ trực
    private boolean quyenSuKien;      // Quản lý sự kiện & vị trí hỗ trợ
    private boolean quyenTruyenThong; // Giao task truyền thông & duyệt bài
    private boolean quyenXuatBaoCao;  // Xem thống kê & xuất báo cáo

    public PhanQuyen() {}

    public PhanQuyen(String tenDangNhap, boolean quyenDoiNgoai, boolean quyenSuKien, boolean quyenTruyenThong, boolean quyenXuatBaoCao) {
        this.tenDangNhap = tenDangNhap;
        this.quyenDoiNgoai = quyenDoiNgoai;
        this.quyenSuKien = quyenSuKien;
        this.quyenTruyenThong = quyenTruyenThong;
        this.quyenXuatBaoCao = quyenXuatBaoCao;
    }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public boolean isQuyenDoiNgoai() { return quyenDoiNgoai; }
    public void setQuyenDoiNgoai(boolean quyenDoiNgoai) { this.quyenDoiNgoai = quyenDoiNgoai; }

    public boolean isQuyenSuKien() { return quyenSuKien; }
    public void setQuyenSuKien(boolean quyenSuKien) { this.quyenSuKien = quyenSuKien; }

    public boolean isQuyenTruyenThong() { return quyenTruyenThong; }
    public void setQuyenTruyenThong(boolean quyenTruyenThong) { this.quyenTruyenThong = quyenTruyenThong; }

    public boolean isQuyenXuatBaoCao() { return quyenXuatBaoCao; }
    public void setQuyenXuatBaoCao(boolean quyenXuatBaoCao) { this.quyenXuatBaoCao = quyenXuatBaoCao; }
}