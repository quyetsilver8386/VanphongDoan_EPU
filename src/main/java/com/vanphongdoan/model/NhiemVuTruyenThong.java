package com.vanphongdoan.model;

import java.sql.Date;

public class NhiemVuTruyenThong {
    private int idNhiemVu;
    private String tenNhiemVu;
    private String maSVDuocGiao;
    private Date hanChot;
    private String trangThai;

    public NhiemVuTruyenThong() {}

    public int getIdNhiemVu() { return idNhiemVu; }
    public void setIdNhiemVu(int idNhiemVu) { this.idNhiemVu = idNhiemVu; }

    public String getTenNhiemVu() { return tenNhiemVu; }
    public void setTenNhiemVu(String tenNhiemVu) { this.tenNhiemVu = tenNhiemVu; }

    public String getMaSVDuocGiao() { return maSVDuocGiao; }
    public void setMaSVDuocGiao(String maSVDuocGiao) { this.maSVDuocGiao = maSVDuocGiao; }

    public Date getHanChot() { return hanChot; }
    public void setHanChot(Date hanChot) { this.hanChot = hanChot; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}