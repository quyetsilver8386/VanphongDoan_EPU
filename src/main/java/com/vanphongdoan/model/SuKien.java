package com.vanphongdoan.model;

public class SuKien {
    private int idSuKien;
    private String tenSuKien;
    private String ngayToChuc;
    private String diaDiem;
    private double kinhPhi;
    private String trangThai;

    public SuKien() {
    }

    public SuKien(int idSuKien, String tenSuKien, String ngayToChuc, String diaDiem, double kinhPhi, String trangThai) {
        this.idSuKien = idSuKien;
        this.tenSuKien = tenSuKien;
        this.ngayToChuc = ngayToChuc;
        this.diaDiem = diaDiem;
        this.kinhPhi = kinhPhi;
        this.trangThai = trangThai;
    }

    public int getIdSuKien() {
        return idSuKien;
    }

    public void setIdSuKien(int idSuKien) {
        this.idSuKien = idSuKien;
    }

    public String getTenSuKien() {
        return tenSuKien;
    }

    public void setTenSuKien(String tenSuKien) {
        this.tenSuKien = tenSuKien;
    }

    public String getNgayToChuc() {
        return ngayToChuc;
    }

    public void setNgayToChuc(String ngayToChuc) {
        this.ngayToChuc = ngayToChuc;
    }

    public String getDiaDiem() {
        return diaDiem;
    }

    public void setDiaDiem(String diaDiem) {
        this.diaDiem = diaDiem;
    }

    public double getKinhPhi() {
        return kinhPhi;
    }
    public void setKinhPhi(double kinhPhi) {
        this.kinhPhi = kinhPhi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
