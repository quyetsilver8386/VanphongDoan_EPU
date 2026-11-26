package com.vanphongdoan.view.user;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.model.NhanSu;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ThongTinCaNhanDialog extends JDialog {
    private NhanSu currentUser;
    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    public ThongTinCaNhanDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "HỒ SƠ CÁ NHÂN THÀNH VIÊN", true);
        this.currentUser = currentUser;

        setSize(520, 490);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // --- 1. BANNER HEADER ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(13, 110, 253));
        pnlHeader.setBorder(new EmptyBorder(16, 25, 16, 25));

        JLabel lblTitle = new JLabel("HỒ SƠ CÁ NHÂN THÀNH VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. THÂN GIAO DIỆN HỒ SƠ ---
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(new Color(245, 247, 250));
        pnlMain.setBorder(new EmptyBorder(20, 25, 20, 25));

        String hoTenDisplay = currentUser != null ? currentUser.getHoTen() : "Thành viên";
        if (hoTenDisplay != null && hoTenDisplay.contains("(")) {
            hoTenDisplay = hoTenDisplay.substring(0, hoTenDisplay.indexOf("(")).trim();
        }

        // Lấy chữ cái đầu làm Avatar
        String firstLetter = "U";
        if (hoTenDisplay != null && !hoTenDisplay.trim().isEmpty()) {
            String[] parts = hoTenDisplay.trim().split("\\s+");
            firstLetter = parts[parts.length - 1].substring(0, 1).toUpperCase();
        }

        // -- Khối Avatar & Họ Tên Summary --
        JPanel pnlAvatar = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        pnlAvatar.setOpaque(false);

        final String initialText = firstLetter;
        JPanel pnlCircleAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(13, 110, 253));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initialText)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initialText, x, y);
            }
        };
        pnlCircleAvatar.setPreferredSize(new Dimension(64, 64));
        pnlCircleAvatar.setOpaque(false);

        JPanel pnlNameRole = new JPanel();
        pnlNameRole.setLayout(new BoxLayout(pnlNameRole, BoxLayout.Y_AXIS));
        pnlNameRole.setOpaque(false);

        JLabel lblName = new JLabel(hoTenDisplay);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(new Color(33, 37, 41));

        String tenBanDisplay = getTenBanFormatted(currentUser);
        JLabel lblRoleBadge = new JLabel("  " + tenBanDisplay + "  ");
        lblRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRoleBadge.setOpaque(true);
        lblRoleBadge.setBackground(new Color(220, 248, 230));
        lblRoleBadge.setForeground(new Color(25, 135, 84));
        lblRoleBadge.setBorder(BorderFactory.createLineBorder(new Color(160, 225, 180), 1, true));

        pnlNameRole.add(lblName);
        pnlNameRole.add(Box.createVerticalStrut(6));
        pnlNameRole.add(lblRoleBadge);

        pnlAvatar.add(pnlCircleAvatar);
        pnlAvatar.add(pnlNameRole);

        pnlMain.add(pnlAvatar);
        pnlMain.add(Box.createVerticalStrut(18));

        // -- Thẻ thông tin chi tiết --
        JPanel pnlDetails = new JPanel(new GridBagLayout());
        pnlDetails.setBackground(Color.WHITE);
        pnlDetails.setBorder(new CompoundBorder(
                new LineBorder(new Color(222, 226, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addInfoRow(pnlDetails, gbc, 0, "Mã SV / TK:", currentUser != null ? currentUser.getMaSV() : "");
        addInfoRow(pnlDetails, gbc, 1, "Họ và Tên:", hoTenDisplay);
        addInfoRow(pnlDetails, gbc, 2, "Lớp / Chi đoàn:", currentUser != null ? currentUser.getLop() : "");
        addInfoRow(pnlDetails, gbc, 3, "Số điện thoại:", currentUser != null ? currentUser.getSoDienThoai() : "");
        addInfoRow(pnlDetails, gbc, 4, "Ban:", tenBanDisplay);

        pnlMain.add(pnlDetails);
        add(pnlMain, BorderLayout.CENTER);

        // --- 3. FOOTER NÚT BẤM ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton btnDoiMatKhau = new JButton("Đổi mật khẩu");
        btnDoiMatKhau.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDoiMatKhau.setBackground(new Color(13, 110, 253));
        btnDoiMatKhau.setForeground(Color.WHITE);
        btnDoiMatKhau.setPreferredSize(new Dimension(140, 36));
        btnDoiMatKhau.setFocusPainted(false);
        btnDoiMatKhau.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnDong.setBackground(new Color(108, 117, 125));
        btnDong.setForeground(Color.WHITE);
        btnDong.setPreferredSize(new Dimension(90, 36));
        btnDong.setFocusPainted(false);
        btnDong.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDoiMatKhau.addActionListener(e -> moDialogDoiMatKhau());
        btnDong.addActionListener(e -> dispose());

        pnlBottom.add(btnDoiMatKhau);
        pnlBottom.add(btnDong);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String valueText) {
        gbc.gridy = row;

        // Cột 1 (Nhãn): In đậm hoàn toàn
        JLabel lblLabel = new JLabel(labelText);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLabel.setForeground(new Color(33, 37, 41));

        // Cột 2 (Giá trị): Chữ thường (PLAIN) hoàn toàn
        JLabel lblValue = new JLabel(valueText != null && !valueText.trim().isEmpty() ? valueText : "Chưa cập nhật");
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblValue.setForeground(new Color(33, 37, 41));

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(7, 5, 7, 25);
        panel.add(lblLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(7, 0, 7, 5);
        panel.add(lblValue, gbc);
    }

    private String getTenBanFormatted(NhanSu user) {
        if (user == null) return "Ban Đối ngoại";

        if (user.getTenBan() != null && !user.getTenBan().trim().isEmpty() && !user.getTenBan().contains("chung")) {
            return user.getTenBan();
        }

        int idBan = user.getIdBan();
        switch (idBan) {
            case 1: return "Ban Đối ngoại";
            case 2: return "Ban Sự kiện";
            case 3: return "Ban Truyền thông";
            default: return "Ban Đối ngoại";
        }
    }

    private void moDialogDoiMatKhau() {
        JPasswordField txtMatKhauCu = new JPasswordField();
        JPasswordField txtMatKhauMoi = new JPasswordField();
        JPasswordField txtXacNhan = new JPasswordField();

        Object[] message = {
            "Mật khẩu hiện tại:", txtMatKhauCu,
            "Mật khẩu mới:", txtMatKhauMoi,
            "Xác nhận mật khẩu mới:", txtXacNhan
        };

        int option = JOptionPane.showConfirmDialog(this, message, "ĐỔI MẬT KHẨU CÁ NHÂN", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String oldPass = new String(txtMatKhauCu.getPassword()).trim();
            String newPass = new String(txtMatKhauMoi.getPassword()).trim();
            String confirmPass = new String(txtXacNhan.getPassword()).trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới và Xác nhận mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nhanSuDAO.doiMatKhau(currentUser.getMaSV(), oldPass, newPass)) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không chính xác!", "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame dummyFrame = new JFrame();
            NhanSu dummyUser = new NhanSu();
            dummyUser.setMaSV("TV01");
            dummyUser.setHoTen("Trần Thị B");
            dummyUser.setLop("D19CNPM2");
            dummyUser.setSoDienThoai("0933445566");
            dummyUser.setIdBan(1);

            ThongTinCaNhanDialog dialog = new ThongTinCaNhanDialog(dummyFrame, dummyUser);
            dialog.setVisible(true);
            System.exit(0);
        });
    }
}