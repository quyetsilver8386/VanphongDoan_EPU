package com.vanphongdoan.view.user;

import com.vanphongdoan.dao.CaTrucDAO;
import com.vanphongdoan.model.CaTruc;
import com.vanphongdoan.model.NhanSu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DangKyCaTrucDialog extends JDialog {
    private CaTrucDAO caTrucDAO = new CaTrucDAO();
    private NhanSu currentUser;

    public DangKyCaTrucDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "ĐĂNG KÝ CA TRỰC VĂN PHÒNG ĐOÀN", true);
        this.currentUser = currentUser;
        setSize(760, 440);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] cols = {"Mã Ca", "Buổi trực", "Ngày trực", "Thứ", "Trạng thái", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);

        // Load dữ liệu
        List<CaTruc> list = caTrucDAO.getAllCaTruc();
        for (CaTruc ct : list) {
            boolean isMine = currentUser.getMaSV().equalsIgnoreCase(ct.getMaSV());
            if ("Mở đăng ký".equalsIgnoreCase(ct.getTrangThai()) || isMine) {
                model.addRow(new Object[]{
                    ct.getIdCa(), ct.getBuoiTruc(), ct.getNgayTruc(), ct.getThu(),
                    isMine ? "Đã đăng ký" : ct.getTrangThai(), ct.getGhiChu() != null ? ct.getGhiChu() : ""
                });
            }
        }

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnDangKy = new JButton("Xác nhận Đăng ký ca chọn");
        btnDangKy.setBackground(new Color(0, 102, 204));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnDangKy.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một ca trực từ danh sách!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idCa = Integer.parseInt(model.getValueAt(row, 0).toString());
            String buoiTruc = model.getValueAt(row, 1).toString();
            String ngayTruc = model.getValueAt(row, 2).toString();
            String thu = model.getValueAt(row, 3).toString();
            String status = model.getValueAt(row, 4).toString();
            String ghiChu = model.getValueAt(row, 5).toString();

            if ("Đã đăng ký".equals(status)) {
                JOptionPane.showMessageDialog(this, "Bạn đã đăng ký ca trực này rồi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            CaTruc ct = new CaTruc();
            ct.setIdCa(idCa);
            ct.setBuoiTruc(buoiTruc);
            ct.setNgayTruc(ngayTruc);
            ct.setThu(thu);
            ct.setMaSV(currentUser.getMaSV());
            ct.setHoTen(currentUser.getHoTen());
            ct.setTrangThai("Đã phân công");
            ct.setGhiChu(ghiChu);

            if (caTrucDAO.update(ct)) {
                JOptionPane.showMessageDialog(this, "Đăng ký ca trực thành công!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Vui lòng thử lại sau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlBottom.add(btnDangKy);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }
}
