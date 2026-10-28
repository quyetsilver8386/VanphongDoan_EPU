package com.vanphongdoan.view.user;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.model.NhanSu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ThongTinCaNhanDialog extends JDialog {
    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    public ThongTinCaNhanDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "HỒ SƠ CÁ NHÂN & ĐỔI MẬT KHẨU", true);
        setSize(440, 370);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlInfo = new JPanel(new GridLayout(6, 2, 8, 8));
        pnlInfo.setBorder(new EmptyBorder(15, 25, 10, 25));

        pnlInfo.add(new JLabel("Mã Sinh Viên / TK:"));
        pnlInfo.add(new JLabel("<html><b>" + currentUser.getMaSV() + "</b></html>"));

        pnlInfo.add(new JLabel("Họ và Tên:"));
        pnlInfo.add(new JLabel(currentUser.getHoTen()));

        pnlInfo.add(new JLabel("Lớp / Chi đoàn:"));
        pnlInfo.add(new JLabel(currentUser.getLop() != null ? currentUser.getLop() : "Chưa cập nhật"));

        pnlInfo.add(new JLabel("Số điện thoại:"));
        pnlInfo.add(new JLabel(currentUser.getSoDienThoai() != null ? currentUser.getSoDienThoai() : "Chưa cập nhật"));

        pnlInfo.add(new JLabel("Ban Chuyên Môn:"));
        pnlInfo.add(new JLabel(currentUser.getTenBan() != null ? currentUser.getTenBan() : "Thành viên chung"));

        pnlInfo.add(new JLabel("Vai trò hệ thống:"));
        pnlInfo.add(new JLabel("Thành viên Đoàn"));

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnDoiMK = new JButton("Đổi mật khẩu");
        JButton btnDong = new JButton("Đóng");

        btnDoiMK.addActionListener(e -> {
            String mkMoi = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới của bạn:", "Đổi mật khẩu", JOptionPane.QUESTION_MESSAGE);
            if (mkMoi != null && !mkMoi.trim().isEmpty()) {
                if (nhanSuDAO.resetMatKhau(currentUser.getMaSV(), mkMoi.trim())) {
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDong.addActionListener(e -> this.dispose());

        pnlBottom.add(btnDoiMK);
        pnlBottom.add(btnDong);

        add(pnlInfo, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }
}