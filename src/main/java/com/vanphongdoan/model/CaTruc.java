package com.vanphongdoan.model;

public class CaTruc {
    private int idCa;
    private String buoiTruc;
    private String ngayTruc;
    private String thu;
    private String maSV;
    private String hoTen;
    private String trangThai;
    private String ghiChu;

    public CaTruc() {
    }

    public CaTruc(int idCa, String buoiTruc, String ngayTruc, String thu, String maSV, String hoTen, String trangThai, String ghiChu) {
        this.idCa = idCa;
        this.buoiTruc = buoiTruc;
        this.ngayTruc = ngayTruc;
        this.thu = thu;
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public int getIdCa() {
        return idCa;
    }

    public void setIdCa(int idCa) {
        this.idCa = idCa;
    }

    public String getBuoiTruc() {
        return buoiTruc;
    }

    public void setBuoiTruc(String buoiTruc) {
        this.buoiTruc = buoiTruc;
    }

    public String getNgayTruc() {
        return ngayTruc;
    }

    public void setNgayTruc(String ngayTruc) {
        this.ngayTruc = ngayTruc;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}