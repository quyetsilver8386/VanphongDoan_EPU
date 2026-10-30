package com.vanphongdoan.view;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.admin.NhanSuView;
import com.vanphongdoan.view.user.DashboardView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

public class LoginView extends JFrame implements ActionListener {

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JCheckBox chkHienMatKhau;
    private JButton btnDangNhap;
    private JButton btnThoat;
    private JLabel lblImage;

    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    public LoginView() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("ĐĂNG NHẬP - HỆ THỐNG QUẢN LÝ VĂN PHÒNG ĐOÀN EPU");
        setSize(760, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel pnlLeftBanner = new JPanel();
        pnlLeftBanner.setBackground(new Color(0, 86, 150));
        pnlLeftBanner.setPreferredSize(new Dimension(320, 0));
        pnlLeftBanner.setLayout(new BoxLayout(pnlLeftBanner, BoxLayout.Y_AXIS));
        pnlLeftBanner.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblLogoText = new JLabel("ĐOÀN TNCS HỒ CHÍ MINH");
        lblLogoText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblLogoText.setForeground(new Color(255, 204, 0));
        lblLogoText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSchool = new JLabel("TRƯỜNG ĐẠI HỌC ĐIỆN LỰC");
        lblSchool.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSchool.setForeground(Color.WHITE);
        lblSchool.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("VĂN PHÒNG ĐOÀN THANH NIÊN");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSub.setForeground(new Color(220, 235, 255));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblImage = new JLabel();
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblImage.setPreferredSize(new Dimension(100, 100));
        lblImage.setMaximumSize(new Dimension(100, 100));
        loadCustomImage();

        JLabel lblSlogan = new JLabel("<html><center><br><b>HỆ THỐNG ĐIỀU HÀNH TÁC NGHIỆP</b><br>"
                + "• Quản lý Lịch trực văn phòng<br>"
                + "• Điều phối Sự kiện & Phong trào<br>"
                + "• Quản lý Nhiệm vụ Truyền thông</center></html>");
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSlogan.setForeground(new Color(240, 240, 240));
        lblSlogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlLeftBanner.add(lblLogoText);
        pnlLeftBanner.add(Box.createVerticalStrut(4));
        pnlLeftBanner.add(lblSchool);
        pnlLeftBanner.add(Box.createVerticalStrut(4));
        pnlLeftBanner.add(lblSub);
        pnlLeftBanner.add(Box.createVerticalStrut(15));
        pnlLeftBanner.add(lblImage);
        pnlLeftBanner.add(Box.createVerticalStrut(10));
        pnlLeftBanner.add(lblSlogan);

        add(pnlLeftBanner, BorderLayout.WEST);

        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBackground(Color.WHITE);
        pnlRight.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlTitle.setOpaque(false);
        JLabel lblLogin = new JLabel("ĐĂNG NHẬP HỆ THỐNG");
        lblLogin.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogin.setForeground(new Color(0, 86, 150));
        pnlTitle.add(lblLogin);
        pnlRight.add(pnlTitle, BorderLayout.NORTH);

        JPanel pnlInputs = new JPanel(new GridBagLayout());
        pnlInputs.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        txtTenDangNhap = new JTextField();
        txtTenDangNhap.setPreferredSize(new Dimension(240, 34));
        txtTenDangNhap.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        txtMatKhau = new JPasswordField();
        txtMatKhau.setPreferredSize(new Dimension(240, 34));
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        chkHienMatKhau = new JCheckBox("Hiển thị mật khẩu");
        chkHienMatKhau.setOpaque(false);
        chkHienMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        chkHienMatKhau.addActionListener(e -> {
            if (chkHienMatKhau.isSelected()) {
                txtMatKhau.setEchoChar((char) 0);
            } else {
                txtMatKhau.setEchoChar('•');
            }
        });

        txtMatKhau.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    xuLyDangNhap();
                }
            }
        });

        JLabel lblUserTag = new JLabel("Tên đăng nhập / Mã SV:");
        lblUserTag.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        pnlInputs.add(lblUserTag, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlInputs.add(txtTenDangNhap, gbc);

        JLabel lblPassTag = new JLabel("Mật khẩu:");
        lblPassTag.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 2;
        pnlInputs.add(lblPassTag, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        pnlInputs.add(txtMatKhau, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.insets = new Insets(2, 0, 15, 0);
        pnlInputs.add(chkHienMatKhau, gbc);

        JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlButtons.setOpaque(false);
        pnlButtons.setPreferredSize(new Dimension(240, 36));

        btnDangNhap = new JButton("Đăng nhập");
        btnThoat = new JButton("Thoát");

        btnDangNhap.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDangNhap.setBackground(new Color(0, 102, 204));
        btnDangNhap.setForeground(Color.WHITE);

        btnThoat.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnDangNhap.addActionListener(this);
        btnThoat.addActionListener(this);

        pnlButtons.add(btnDangNhap);
        pnlButtons.add(btnThoat);

        gbc.gridx = 0; gbc.gridy = 5;
        pnlInputs.add(pnlButtons, gbc);

        pnlRight.add(pnlInputs, BorderLayout.CENTER);
        add(pnlRight, BorderLayout.CENTER);
    }

    private void loadCustomImage() {
        ImageIcon icon = null;
        try {
            URL imgUrl = getClass().getResource("/logo.png");
            if (imgUrl != null) {
                icon = new ImageIcon(imgUrl);
            } else {
                File file = new File("logo.png");
                if (file.exists()) {
                    icon = new ImageIcon("logo.png");
                }
            }

            if (icon != null) {
                Image img = icon.getImage().getScaledInstance(95, 95, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
            } else {
                lblImage.setText("★ EPU Đoàn ★");
                lblImage.setFont(new Font("Segoe UI", Font.BOLD, 15));
                lblImage.setForeground(new Color(255, 204, 0));
                lblImage.setHorizontalAlignment(SwingConstants.CENTER);
                lblImage.setBorder(BorderFactory.createDashedBorder(new Color(255, 204, 0), 1, 4, 2, true));
            }
        } catch (Exception e) {
            lblImage.setText("");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnThoat) {
            System.exit(0);
        } else if (e.getSource() == btnDangNhap) {
            xuLyDangNhap();
        }
    }

    private void xuLyDangNhap() {
        String username = txtTenDangNhap.getText().trim();
        String password = new String(txtMatKhau.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnDangNhap.setEnabled(false);

        new Thread(() -> {
            try {
                NhanSu user = nhanSuDAO.checkLogin(username, password);

                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    btnDangNhap.setEnabled(true);

                    if (user == null) {
                        JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không chính xác!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (!user.isTrangThai()) {
                        JOptionPane.showMessageDialog(this, "Tài khoản của bạn đang bị TẠM KHÓA!\nVui lòng liên hệ Admin Văn phòng Đoàn.", "Khóa tài khoản", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!\nXin chào: " + user.getHoTen());
                    this.dispose();

                    // --- PHÂN QUYỀN ĐIỀU HƯỚNG CHUẨN XÁC THEO VAI TRÒ ---
                    if (user.getIdVaiTro() == 1 || user.getIdVaiTro() == 2) {
                        new NhanSuView(user).setVisible(true); // Admin hoặc Ban Điều hành
                    } else {
                        new DashboardView(user).setVisible(true); // Thành viên
                    if (user.getIdVaiTro() == 1 || user.getIdVaiTro() == 2) {
                        new NhanSuView(user).setVisible(true);
                    } else {
                        new DashboardView(user).setVisible(true);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    setCursor(Cursor.getDefaultCursor());
                    btnDangNhap.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Không thể kết nối CSDL: " + ex.getMessage(), "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
                });
                ex.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}