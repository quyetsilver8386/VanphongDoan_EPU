package com.vanphongdoan.view.user;

import com.vanphongdoan.dao.SuKienDAO;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.model.SuKien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DangKySuKienDialog extends JDialog {
    private SuKienDAO suKienDAO = new SuKienDAO();
    private NhanSu currentUser;
    private JTable tableSuKien;
    private DefaultTableModel tableModel;
    private List<SuKien> listSuKienCurrent = new ArrayList<>();

    public DangKySuKienDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "DANH SÁCH SỰ KIỆN ĐANG MỞ ĐĂNG KÝ", true);
        this.currentUser = currentUser;
        setSize(1200, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(12, 12));

        // --- 1. HEADER HƯỚNG DẪN ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(new EmptyBorder(15, 18, 8, 18));
        JLabel lblHuongDan = new JLabel("<html><b>Danh sách sự kiện Đoàn:</b> Click chọn 1 sự kiện để Đăng ký vị trí (MC, Lễ tân, Chạy sự kiện, Hậu cần...) hoặc Gửi yêu cầu xin hủy.</html>");
        lblHuongDan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlHeader.add(lblHuongDan, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. BẢNG SỰ KIỆN ---
        String[] cols = {"Mã SK", "Tên sự kiện", "Ngày tổ chức", "Địa điểm", "Trạng thái / Vị trí"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableSuKien = new JTable(tableModel);
        tableSuKien.setRowHeight(85); 
        tableSuKien.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tableSuKien.setShowGrid(true);
        tableSuKien.setGridColor(new Color(210, 210, 210));

        tableSuKien.setCellSelectionEnabled(true);
        tableSuKien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableSuKien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableSuKien.getTableHeader().setBackground(new Color(230, 240, 250));
        tableSuKien.getTableHeader().setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tableSuKien.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < cols.length; i++) {
            tableSuKien.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tableSuKien.getColumnModel().getColumn(0).setPreferredWidth(70);
        tableSuKien.getColumnModel().getColumn(0).setMaxWidth(90);
        tableSuKien.getColumnModel().getColumn(1).setPreferredWidth(320);

        loadDataToGrid();

        // --- 3. FOOTER NÚT BẤM ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 15));

        JButton btnHuyDangKy = new JButton("Gửi yêu cầu xin hủy ca");
        btnHuyDangKy.setBackground(new Color(204, 102, 0));
        btnHuyDangKy.setForeground(Color.WHITE);
        btnHuyDangKy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuyDangKy.setPreferredSize(new Dimension(210, 38));
        btnHuyDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDangKy = new JButton("Đăng ký tham gia Sự kiện");
        btnDangKy.setBackground(new Color(40, 167, 69)); 
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangKy.setPreferredSize(new Dimension(220, 38));
        btnDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDangKy.addActionListener(e -> {
            int row = tableSuKien.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn 1 ô sự kiện trong bảng để đăng ký!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            SuKien sk = listSuKienCurrent.get(row);
            if (sk.getViTriDangKy() != null && !sk.getViTriDangKy().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn đã đăng ký tham gia sự kiện này với vị trí: " + sk.getViTriDangKy(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // TÁCH CHUỖI VỊ TRÍ ĐỘNG DO ADMIN THIẾT LẬP DÀNH RIÊNG CHO SỰ KIỆN NÀY
            String rawViTri = sk.getDanhSachViTri();
            if (rawViTri == null || rawViTri.trim().isEmpty()) {
                rawViTri = "MC, Lễ tân, Chạy sự kiện, Hậu cần"; // Mặc định nếu trống
            }
            String[] viTriOptions = rawViTri.split("\\s*,\\s*"); // Tách dấu phẩy và tự động xóa khoảng trắng thừa

            String selectedViTri = (String) JOptionPane.showInputDialog(
                    this,
                    "Vui lòng chọn vị trí bạn muốn đảm nhận trong sự kiện [" + sk.getTenSuKien() + "]:",
                    "Chọn vị trí tham gia",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    viTriOptions,
                    viTriOptions[0]
            );

            if (selectedViTri != null) {
                if (suKienDAO.dangKySuKien(currentUser.getMaSV(), sk.getIdSuKien(), selectedViTri)) {
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công vị trí [" + selectedViTri + "] cho sự kiện " + sk.getTenSuKien() + "!");
                    loadDataToGrid();
                } else {
                    JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // Xử lý gửi yêu cầu Xin Hủy (Mở XinHuySuKienDialog)
        btnHuyDangKy.addActionListener(e -> {
            int row = tableSuKien.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn 1 ô sự kiện bạn muốn xin hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            SuKien sk = listSuKienCurrent.get(row);
            if (sk.getViTriDangKy() == null || sk.getViTriDangKy().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bạn chưa đăng ký tham gia sự kiện này!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if ("Chờ hủy".equals(sk.getTrangThaiDangKy())) {
                JOptionPane.showMessageDialog(this, "Yêu cầu xin hủy sự kiện này của bạn đang chờ Ban Điều Hành duyệt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            XinHuySuKienDialog dialog = new XinHuySuKienDialog(this, sk, currentUser, suKienDAO);
            dialog.setVisible(true);
            if (dialog.isSubmitted()) {
                loadDataToGrid();
            }
        });

        pnlBottom.add(btnHuyDangKy);
        pnlBottom.add(btnDangKy);
        add(new JScrollPane(tableSuKien), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadDataToGrid() {
        tableModel.setRowCount(0);
        listSuKienCurrent = suKienDAO.getAllSuKienForUser(currentUser.getMaSV());

        if (listSuKienCurrent == null || listSuKienCurrent.isEmpty()) {
            listSuKienCurrent = new ArrayList<>();
            listSuKienCurrent.add(new SuKien(1, "Chào Tân Sinh Viên Khoa CNTT 2026", "2026-11-05", "Hội trường A1", 15000000, "Sắp diễn ra"));
            listSuKienCurrent.add(new SuKien(2, "Giải Bóng Đá Cúp Thanh Niên EPU", "2026-11-12", "Sân bóng EPU", 20000000, "Sắp diễn ra"));
            listSuKienCurrent.add(new SuKien(3, "Hiến Máu Nhân Đạo Giọt Hồng EPU", "2026-11-20", "Sảnh nhà B", 5000000, "Sắp diễn ra"));
        }

        for (SuKien sk : listSuKienCurrent) {
            String trangThaiHtml;
            if ("Chờ hủy".equals(sk.getTrangThaiDangKy())) {
                // Hiển thị trạng thái đang chờ BĐH duyệt màu cam & chống ngắt dòng vỡ chữ
                trangThaiHtml = "<html><center><font size='4' color='#d97706'><b>[Đang chờ<br>BĐH&nbsp;duyệt&nbsp;hủy]</b></font><br>"
                              + "<font size='3' color='#333333'>📍 Vị trí: <b>" + sk.getViTriDangKy() + "</b></font></center></html>";
            } else if (sk.getViTriDangKy() != null && !sk.getViTriDangKy().trim().isEmpty()) {
                trangThaiHtml = "<html><center><font size='4' color='#008000'><b>[Đã đăng ký]</b></font><br>"
                              + "<font size='3' color='#333333'>📍 Vị trí: <b>" + sk.getViTriDangKy() + "</b></font></center></html>";
            } else {
                trangThaiHtml = "<html><center><font size='4' color='#0044cc'><b>[Mở đăng ký]</b></font><br>"
                              + "<font size='2.5' color='#666666'>" + (sk.getTrangThai() != null ? sk.getTrangThai() : "Sắp diễn ra") + "</font></center></html>";
            }

            Object[] row = {
                sk.getIdSuKien(),
                "<html><left><b>" + sk.getTenSuKien() + "</b></left></html>",
                sk.getNgayToChuc(),
                sk.getDiaDiem(),
                trangThaiHtml
            };
            tableModel.addRow(row);
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

            DangKySuKienDialog dialog = new DangKySuKienDialog(dummyFrame, dummyUser);
            dialog.setVisible(true);
            System.exit(0);
        });
    }
}