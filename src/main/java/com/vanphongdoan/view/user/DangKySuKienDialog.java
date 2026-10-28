package com.vanphongdoan.view.user;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.NhanSu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DangKySuKienDialog extends JDialog {
    public DangKySuKienDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "DANH SÁCH SỰ KIỆN ĐANG MỞ ĐĂNG KÝ", true);
        setSize(740, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] cols = {"Mã SK", "Tên sự kiện", "Ngày tổ chức", "Địa điểm", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM SuKien");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID_SuKien"), rs.getString("TenSuKien"),
                    rs.getDate("NgayToChuc"), rs.getString("DiaDiem"), rs.getString("TrangThai")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnDangKySK = new JButton("Đăng ký tham gia Sự kiện");
        btnDangKySK.setBackground(new Color(40, 167, 69));
        btnDangKySK.setForeground(Color.WHITE);
        btnDangKySK.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnDangKySK.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sự kiện muốn đăng ký!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String tenSK = model.getValueAt(row, 1).toString();
            JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu đăng ký tham gia sự kiện:\n[" + tenSK + "]\nBan Điều hành sẽ duyệt danh sách sớm nhất!");
            this.dispose();
        });

        pnlBottom.add(btnDangKySK);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }
}