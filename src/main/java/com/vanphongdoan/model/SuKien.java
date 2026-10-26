package com.vanphongdoan.model;

import java.sql.Date;

public class SuKien {
    private int idSuKien;
    private String tenSuKien;
    private Date ngayToChuc;
    private String diaDiem;
    private String moTa;

    public SuKien() {}

    public int getIdSuKien() { return idSuKien; }
    public void setIdSuKien(int idSuKien) { this.idSuKien = idSuKien; }

    public String getTenSuKien() { return tenSuKien; }
    public void setTenSuKien(String tenSuKien) { this.tenSuKien = tenSuKien; }

    public Date getNgayToChuc() { return ngayToChuc; }
    public void setNgayToChuc(Date ngayToChuc) { this.ngayToChuc = ngayToChuc; }

    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String diaDiem) { this.diaDiem = diaDiem; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}