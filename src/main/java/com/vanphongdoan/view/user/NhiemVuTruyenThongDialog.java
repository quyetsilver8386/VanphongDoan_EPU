package com.vanphongdoan.view.user;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.NhanSu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NhiemVuTruyenThongDialog extends JDialog {
    public NhiemVuTruyenThongDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "NHIỆM VỤ TRUYỀN THÔNG ĐƯỢC GIAO", true);
        setSize(720, 380);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] cols = {"Mã NV", "Tên nhiệm vụ / Bài viết", "Hạn chót", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);

        String sql = "SELECT * FROM NhiemVuTruyenThong WHERE MaSV_DuocGiao = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentUser.getMaSV());
            ResultSet rs = ps.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                model.addRow(new Object[]{
                    rs.getInt("ID_NhiemVu"), rs.getString("TenNhiemVu"),
                    rs.getDate("HanChot"), rs.getString("TrangThai")
                });
            }
            if (!hasData) {
                model.addRow(new Object[]{"-", "Bạn chưa có nhiệm vụ truyền thông nào được giao", "-", "-"});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}