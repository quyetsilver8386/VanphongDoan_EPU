package com.vanphongdoan.view.user;

import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;
import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {

    private NhanSu currentUser;

    public DashboardView(NhanSu ns) {
        this.currentUser = ns;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("TRANG CHU - THANH VIEN VAN PHONG DOAN");
        setSize(750, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 153, 76)); // Màu xanh lá Đoàn
        panelHeader.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("HE THONG QUAN LY VAN PHONG DOAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelUser.setOpaque(false);

        JLabel lblWelcome = new JLabel("Xin chào: " + ns.getHoTen() + " (" + ns.getLop() + ")");
        lblWelcome.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblWelcome.setForeground(Color.WHITE);

        JButton btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDangXuat.addActionListener(e -> {
            this.dispose();
            new LoginView().setVisible(true);
        });

        panelUser.add(lblWelcome);
        panelUser.add(btnDangXuat);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(panelUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // Center Menu Buttons
        JPanel panelMenu = new JPanel(new GridLayout(2, 2, 20, 20));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        JButton btnCaTruc = new JButton("Đăng ký Ca trực");
        btnCaTruc.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JButton btnSuKien = new JButton("Đăng ký Sự kiện");
        btnSuKien.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JButton btnTruyenThong = new JButton("Nhiệm vụ Truyền thông");
        btnTruyenThong.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JButton btnCaNhan = new JButton("Thông tin Cá nhân");
        btnCaNhan.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // GẮN SỰ KIỆN KHI THÀNH VIÊN BẤM NÚT
        btnCaTruc.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Mở màn hình Đăng ký ca trực dành cho: " + currentUser.getHoTen());
            // new DangKyCaTrucView(currentUser).setVisible(true);
        });

        btnSuKien.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Mở danh sách các sự kiện đang cho phép đăng ký!");
        });

        btnTruyenThong.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Mở danh sách bài viết/nhiệm vụ được giao!");
        });

        btnCaNhan.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Thông tin tài khoản: " + currentUser.getHoTen() + " - SĐT: " + currentUser.getSoDienThoai());
        });

        panelMenu.add(btnCaTruc);
        panelMenu.add(btnSuKien);
        panelMenu.add(btnTruyenThong);
        panelMenu.add(btnCaNhan);

        add(panelMenu, BorderLayout.CENTER);
    }
}