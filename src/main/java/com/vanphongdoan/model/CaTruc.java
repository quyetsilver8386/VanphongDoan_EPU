package com.vanphongdoan.model;

import java.sql.Date;

public class CaTruc {
    private int idCaTruc;
    private String maSV;
    private Date ngayTruc;
    private String buoiTruc;
    private String ghiChu;

    public CaTruc() {}

    public int getIdCaTruc() { return idCaTruc; }
    public void setIdCaTruc(int idCaTruc) { this.idCaTruc = idCaTruc; }

    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public Date getNgayTruc() { return ngayTruc; }
    public void setNgayTruc(Date ngayTruc) { this.ngayTruc = ngayTruc; }

    public String getBuoiTruc() { return buoiTruc; }
    public void setBuoiTruc(String buoiTruc) { this.buoiTruc = buoiTruc; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}