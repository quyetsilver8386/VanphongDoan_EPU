package com.vanphongdoan.view.user;

import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardView extends JFrame {
    private NhanSu currentUser;

    public DashboardView(NhanSu currentUser) {
        this.currentUser = currentUser;
        
        setTitle("HỆ THỐNG QUẢN LÝ VĂN PHÒNG ĐOÀN - TRANG CHỦ");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. HEADER THANH ĐIỀU HÀNH (ĐÃ THÊM NÚT ĐĂNG XUẤT) ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(13, 110, 253));
        pnlHeader.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel lblTitle = new JLabel("BẢNG ĐIỀU HÀNH THÀNH VIÊN ĐOÀN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        // Panel góc phải chứa tên người dùng & Nút Đăng xuất
        JPanel pnlRightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlRightHeader.setOpaque(false);

        String nameDisplay = (currentUser != null && currentUser.getHoTen() != null) ? currentUser.getHoTen() : "Thành viên";
        JLabel lblUser = new JLabel("Xin chào, " + nameDisplay + "!");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(Color.WHITE);

        JButton btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDangXuat.setBackground(new Color(220, 53, 69)); // Màu đỏ nổi bật
        btnDangXuat.setForeground(Color.WHITE);
        btnDangXuat.setFocusPainted(false);
        btnDangXuat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangXuat.setPreferredSize(new Dimension(100, 32));

        // Sự kiện Đăng xuất quay về LoginView
        btnDangXuat.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginView().setVisible(true);
                this.dispose();
            }
        });

        pnlRightHeader.add(lblUser);
        pnlRightHeader.add(btnDangXuat);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlRightHeader, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. GRID 4 NÚT KHỐI MÀU NỔI BẬT ---
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 22, 22));
        pnlButtons.setBackground(new Color(238, 242, 246));
        pnlButtons.setBorder(new EmptyBorder(30, 35, 30, 35));

        JButton btnLichTruc = createSolidButton("ĐĂNG KÝ LỊCH TRỰC", new Color(40, 167, 69));
        JButton btnSuKien = createSolidButton("ĐĂNG KÝ SỰ KIỆN", new Color(13, 110, 253));
        JButton btnNhiemVu = createSolidButton("NHIỆM VỤ TRUYỀN THÔNG", new Color(255, 179, 0));
        JButton btnHoSo = createSolidButton("HỒ SƠ & THỐNG KÊ CÁ NHÂN", new Color(108, 117, 125));

        pnlButtons.add(btnLichTruc);
        pnlButtons.add(btnSuKien);
        pnlButtons.add(btnNhiemVu);
        pnlButtons.add(btnHoSo);

        add(pnlButtons, BorderLayout.CENTER);

        // --- 3. BẮT SỰ KIỆN MỞ DIALOG ---
        btnLichTruc.addActionListener(e -> {
            DangKyCaTrucDialog dialog = new DangKyCaTrucDialog(this, currentUser);
            dialog.setVisible(true);
        });

        btnSuKien.addActionListener(e -> {
            DangKySuKienDialog dialog = new DangKySuKienDialog(this, currentUser);
            dialog.setVisible(true);
        });

        btnHoSo.addActionListener(e -> {
            ThongTinCaNhanDialog dialog = new ThongTinCaNhanDialog(this, currentUser);
            dialog.setVisible(true);
        });

        btnNhiemVu.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chức năng Nhiệm vụ truyền thông đang được hoàn thiện!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private JButton createSolidButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            NhanSu dummyUser = new NhanSu();
            dummyUser.setMaSV("TV01");
            dummyUser.setHoTen("Trần Thị B");

            DashboardView dashboard = new DashboardView(dummyUser);
            dashboard.setVisible(true);
        });
    }
}