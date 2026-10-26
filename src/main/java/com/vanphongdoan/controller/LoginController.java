package com.vanphongdoan.controller;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.model.NhanSu;

public class LoginController {
    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    public NhanSu xuLyDangNhap(String maSV, String matKhau) {
        if (maSV == null || maSV.trim().isEmpty() || matKhau == null || matKhau.trim().isEmpty()) {
            return null;
        }
        return nhanSuDAO.checkLogin(maSV.trim(), matKhau.trim());
    }
}