package com.vanphongdoan.view;

import com.vanphongdoan.controller.LoginController;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.admin.NhanSuView;
import com.vanphongdoan.view.user.DashboardView;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField txtMaSV;
    private JPasswordField txtMatKhau;
    private JButton btnLogin;

    public LoginView() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("DANG NHAP - HE THONG QUAN LY VAN PHONG DOAN");
        setSize(420, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tieu de
        JLabel lblTitle = new JLabel("DANG NHAP HE THONG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        // Ma SV
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblMaSV = new JLabel("Ma sinh vien:");
        lblMaSV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblMaSV, gbc);

        txtMaSV = new JTextField(15);
        txtMaSV.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(txtMaSV, gbc);

        // Mat khau
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblMatKhau = new JLabel("Mat khau:");
        lblMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblMatKhau, gbc);

        txtMatKhau = new JPasswordField(15);
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(txtMatKhau, gbc);

        // Nut Dang nhap
        btnLogin = new JButton("DANG NHAP");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setPreferredSize(new Dimension(150, 35));
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnLogin, gbc);

        add(panel);

        btnLogin.addActionListener(e -> xuLyDangNhap());
    }

    private void xuLyDangNhap() {
        String maSV = txtMaSV.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword()).trim();

        if (maSV.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap day du thong tin!", "Thong bao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LoginController controller = new LoginController();
        NhanSu ns = controller.xuLyDangNhap(maSV, matKhau);

        if (ns != null) {
            JOptionPane.showMessageDialog(this, "Dang nhap thanh cong!\nXin chao: " + ns.getHoTen(), "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            
            // CẬP NHẬT PHÂN QUYỀN TẠI ĐÂY
            if (ns.getIdVaiTro() == 1 || ns.getIdVaiTro() == 2) { 
                // Admin hoặc Ban Điều hành được vào Form Quản lý
                new NhanSuView(ns).setVisible(true);
            } else { 
                // Thành viên chỉ được vào Dashboard
                new DashboardView(ns).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Sai Ma sinh vien hoac Mat khau!", "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}