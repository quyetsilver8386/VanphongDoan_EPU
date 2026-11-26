package com.vanphongdoan.view.user;

import com.vanphongdoan.dao.TruyenThongDAO;
import com.vanphongdoan.model.NhanSu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NhiemVuTruyenThongDialog extends JDialog {
    private TruyenThongDAO truyenThongDAO = new TruyenThongDAO();
    private NhanSu currentUser;
    private JTable table;
    private DefaultTableModel tableModel;

    public NhiemVuTruyenThongDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "NHIỆM VỤ TRUYỀN THÔNG ĐƯỢC GIAO", true);
        this.currentUser = currentUser;
        setSize(980, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // 1. Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(new EmptyBorder(15, 18, 5, 18));
        String tenUser = (currentUser != null && currentUser.getHoTen() != null) ? currentUser.getHoTen() : "Thành viên";
        JLabel lblTitle = new JLabel("<html><b>Danh sách nhiệm vụ truyền thông của:</b> " + tenUser + " <i>(Chọn 1 dòng để cập nhật tiến độ)</i></html>");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlHeader.add(lblTitle, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. Bảng 1 cột duy nhất đồng nhất với TruyenThongView
        String[] cols = {"ID", "Tiêu đề ấn phẩm / bài viết", "Deadline", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 240, 250));
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(250);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(new EmptyBorder(0, 18, 0, 18));
        pnlCenter.add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // 3. Footer nút thao tác
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 12));
        JButton btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setPreferredSize(new Dimension(110, 36));
        btnLamMoi.addActionListener(e -> loadData());

        JButton btnCapNhat = new JButton("Cập nhật trạng thái");
        btnCapNhat.setBackground(new Color(0, 102, 204));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCapNhat.setPreferredSize(new Dimension(180, 36));
        btnCapNhat.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCapNhat.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhiệm vụ trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Object idObj = tableModel.getValueAt(row, 0);
            if (!(idObj instanceof Integer)) return;

            int idNhiemVu = (Integer) idObj;
            String currentStatus = tableModel.getValueAt(row, 3).toString();
            String[] options = {"Đang làm", "Đang chờ", "Đã hoàn thành"};

            String newStatus = (String) JOptionPane.showInputDialog(
                    this,
                    "Cập nhật tiến độ cho: " + tableModel.getValueAt(row, 1),
                    "Cập nhật trạng thái",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    currentStatus
            );

            if (newStatus != null) {
                if (truyenThongDAO.updateStatus(idNhiemVu, newStatus)) {
                    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thành: " + newStatus);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlBottom.add(btnLamMoi);
        pnlBottom.add(btnCapNhat);
        add(pnlBottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        if (currentUser == null) return;

        List<Object[]> list = truyenThongDAO.getByMember(
                currentUser.getMaSV(),
                currentUser.getTenDangNhap(),
                currentUser.getHoTen()
        );

        if (list.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "Bạn chưa được giao nhiệm vụ truyền thông nào", "-", "-", "-"});
            return;
        }

        for (Object[] row : list) {
            tableModel.addRow(row);
        }
    }
}