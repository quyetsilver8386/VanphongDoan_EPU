package com.vanphongdoan.model;

public class NhanSu {
    private String maSV;
    private String matKhau;
    private String hoTen;
    private String lop;
    private String soDienThoai;
    private boolean trangThai;
    private int idVaiTro;
    private Integer idBan;

    public NhanSu() {}

    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public boolean isTrangThai() { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public int getIdVaiTro() { return idVaiTro; }
    public void setIdVaiTro(int idVaiTro) { this.idVaiTro = idVaiTro; }

    public Integer getIdBan() { return idBan; }
    public void setIdBan(Integer idBan) { this.idBan = idBan; }
}