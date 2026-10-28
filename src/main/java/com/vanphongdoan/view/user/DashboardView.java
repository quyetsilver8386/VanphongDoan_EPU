package com.vanphongdoan.view.user;

import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

        setTitle("TRANG CHỦ - THÀNH VIÊN VĂN PHÒNG ĐOÀN EPU");
        setSize(900, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout(20, 0));
        panelHeader.setBackground(new Color(0, 153, 76));
        panelHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG VĂN PHÒNG ĐOÀN EPU");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JPanel panelUser = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelUser.setOpaque(false);

        String hoTenUser = (currentUser != null && currentUser.getHoTen() != null) ? currentUser.getHoTen() : "Thành viên";
        String lopUser = (currentUser != null && currentUser.getLop() != null && !currentUser.getLop().isEmpty()) 
                ? currentUser.getLop() : "Chi đoàn";

        JLabel lblWelcome = new JLabel("Xin chào: " + hoTenUser + " (" + lopUser + ")");
        lblWelcome.setFont(new Font("Segoe UI", Font.ITALIC, 13));
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

        // --- MENU 4 NÚT ---
        JPanel panelMenu = new JPanel(new GridLayout(2, 2, 20, 20));
        panelMenu.setBorder(new EmptyBorder(30, 40, 30, 40));

        JButton btnCaTruc = new JButton("Đăng ký Ca trực");
        JButton btnSuKien = new JButton("Đăng ký Sự kiện");
        JButton btnTruyenThong = new JButton("Nhiệm vụ Truyền thông");
        JButton btnCaNhan = new JButton("Thông tin Cá nhân");

        Font btnFont = new Font("Segoe UI", Font.BOLD, 16);
        btnCaTruc.setFont(btnFont); btnSuKien.setFont(btnFont);
        btnTruyenThong.setFont(btnFont); btnCaNhan.setFont(btnFont);
        
        btnCaTruc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSuKien.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTruyenThong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCaNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // CHỈ ĐỊNH 4 NÚT GỌI ĐẾN 4 FILE CLASS VỪA TẠO
        btnCaTruc.addActionListener(e -> new DangKyCaTrucDialog(this, currentUser).setVisible(true));
        btnSuKien.addActionListener(e -> new DangKySuKienDialog(this, currentUser).setVisible(true));
        btnTruyenThong.addActionListener(e -> new NhiemVuTruyenThongDialog(this, currentUser).setVisible(true));
        btnCaNhan.addActionListener(e -> new ThongTinCaNhanDialog(this, currentUser).setVisible(true));

        panelMenu.add(btnCaTruc);
        panelMenu.add(btnSuKien);
        panelMenu.add(btnTruyenThong);
        panelMenu.add(btnCaNhan);

        add(panelMenu, BorderLayout.CENTER);
    }
}