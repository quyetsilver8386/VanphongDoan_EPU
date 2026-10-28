package com.vanphongdoan.model;

public class NhanSu {
    // 1. Nhóm tài khoản xác thực
    private int idNhanSu;
    private String tenDangNhap;
    private String matKhau;
    private int idVaiTro;
    private String tenVaiTro; // Để hiển thị chữ lên bảng JTable
    private boolean trangThai; // true: Hoạt động, false: Tạm khóa

    // 2. Nhóm hồ sơ định danh
    private String maSV;       // Mã sinh viên EPU / Mã cán bộ
    private String hoTen;
    private String khoa;       // Khóa sinh viên: K18, K19...
    private String lop;
    private String soDienThoai;
    private Integer idBan;     // Cho phép null nếu không thuộc ban
    private String tenBan;     // Để hiển thị chữ lên bảng JTable

    public NhanSu() {
    }

    public NhanSu(int idNhanSu, String tenDangNhap, String matKhau, int idVaiTro, boolean trangThai, 
                  String maSV, String hoTen, String khoa, String lop, String soDienThoai, Integer idBan) {
        this.idNhanSu = idNhanSu;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.idVaiTro = idVaiTro;
        this.trangThai = trangThai;
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.khoa = khoa;
        this.lop = lop;
        this.soDienThoai = soDienThoai;
        this.idBan = idBan;
    }

    // --- Getter và Setter ---
    public int getIdNhanSu() { return idNhanSu; }
    public void setIdNhanSu(int idNhanSu) { this.idNhanSu = idNhanSu; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public int getIdVaiTro() { return idVaiTro; }
    public void setIdVaiTro(int idVaiTro) { this.idVaiTro = idVaiTro; }

    public String getTenVaiTro() { return tenVaiTro; }
    public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getKhoa() { return khoa; }
    public void setKhoa(String khoa) { this.khoa = khoa; }

    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public Integer getIdBan() { return idBan; }
    public void setIdBan(Integer idBan) { this.idBan = idBan; }

    public String getTenBan() { return tenBan; }
    public void setTenBan(String tenBan) { this.tenBan = tenBan; }
}