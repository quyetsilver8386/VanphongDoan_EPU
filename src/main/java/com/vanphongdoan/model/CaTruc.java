package com.vanphongdoan.model;

import java.util.Date;

public class CaTruc {
    private int idCa;
    private String tenCa;
    private Date ngayTruc;
    private String maSV;
    private String trangThai;
    private String ghiChu;

    public CaTruc() {}

    public int getIdCa() { return idCa; }
    public void setIdCa(int idCa) { this.idCa = idCa; }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }

    public Date getNgayTruc() { return ngayTruc; }
    public void setNgayTruc(Date ngayTruc) { this.ngayTruc = ngayTruc; }

    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}