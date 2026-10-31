package com.vanphongdoan.view.user;

import com.vanphongdoan.config.DatabaseConnection;
import com.vanphongdoan.model.NhanSu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NhiemVuTruyenThongDialog extends JDialog {
    private JTable tableMoi;
    private DefaultTableModel modelMoi;

    private JTable tableHienTai;
    private DefaultTableModel modelHienTai;

    private NhanSu currentUser;

    public NhiemVuTruyenThongDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "DANH SÁCH NHIỆM VỤ TRUYỀN THÔNG ĐƯỢC GIAO", true);
        this.currentUser = currentUser;
        setSize(1200, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(12, 12));

        // --- 1. HEADER HƯỚNG DẪN ĐỒNG BỘ ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(new EmptyBorder(15, 18, 8, 18));
        JLabel lblHuongDan = new JLabel("<html><b>Nhiệm vụ truyền thông Đoàn:</b> Chọn nhiệm vụ bên trái để <b>Xác nhận tiếp nhận</b>, sau đó theo dõi và <b>Cập nhật tiến độ</b> (Đang chờ, Đang làm, Đã hoàn thành) ở bảng bên phải.</html>");
        lblHuongDan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlHeader.add(lblHuongDan, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. GRID 2 CỘT (TRÁI: NHIỆM VỤ MỚI | PHẢI: NHIỆM VỤ HIỆN TẠI) ---
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlCenter.setBorder(new EmptyBorder(0, 18, 15, 18));

        pnlCenter.add(createLeftPanel());
        pnlCenter.add(createRightPanel());

        add(pnlCenter, BorderLayout.CENTER);

        loadData();
    }

    // --- BÊN TRÁI: NHIỆM VỤ MỚI CẦN TIẾP NHẬN ---
    private JPanel createLeftPanel() {
        JPanel pnl = new JPanel(new BorderLayout(8, 8));
        pnl.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(204, 102, 0), 1),
                " 📢 NHIỆM VỤ MỚI ĐƯỢC PHÂN CÔNG ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(204, 102, 0)
        ));

        String[] cols = {"Mã", "Tên nhiệm vụ / Ấn phẩm", "Hạn chót", "Trạng thái"};
        modelMoi = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableMoi = new JTable(modelMoi);
        setupTableStyle(tableMoi);

        tableMoi.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableMoi.getColumnModel().getColumn(0).setMaxWidth(65);
        tableMoi.getColumnModel().getColumn(1).setPreferredWidth(230);
        tableMoi.getColumnModel().getColumn(2).setPreferredWidth(95);
        tableMoi.getColumnModel().getColumn(3).setPreferredWidth(130);

        pnl.add(new JScrollPane(tableMoi), BorderLayout.CENTER);

        // Nút bấm bên trái
        JPanel pnlBottomLeft = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        JButton btnXacNhan = new JButton("Xác nhận tiếp nhận");
        btnXacNhan.setBackground(new Color(0, 102, 204));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXacNhan.setPreferredSize(new Dimension(190, 38));
        btnXacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnXacNhan.addActionListener(e -> {
            int row = tableMoi.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn 1 nhiệm vụ mới trong bảng để xác nhận tiếp nhận!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object idObj = tableMoi.getValueAt(row, 0);
            if (idObj == null || idObj.toString().trim().equals("-")) {
                return;
            }

            int idNhiemVu = Integer.parseInt(idObj.toString().trim());

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn xác nhận tiếp nhận thực hiện nhiệm vụ này?",
                    "Xác nhận tiếp nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                String updateSql = "UPDATE NhiemVuTruyenThong SET TrangThai = N'Đang làm' WHERE ID_NhiemVu = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, idNhiemVu);
                    int result = ps.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Đã tiếp nhận nhiệm vụ thành công!\nNhiệm vụ đã được chuyển sang danh sách thực hiện.");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Tiếp nhận thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlBottomLeft.add(btnXacNhan);
        pnl.add(pnlBottomLeft, BorderLayout.SOUTH);

        return pnl;
    }

    // --- BÊN PHẢI: NHIỆM VỤ HIỆN TẠI & TIẾN ĐỘ ---
    private JPanel createRightPanel() {
        JPanel pnl = new JPanel(new BorderLayout(8, 8));
        pnl.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(40, 167, 69), 1),
                " 📝 NHIỆM VỤ HIỆN TẠI & TIẾN ĐỘ THỰC HIỆN ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(40, 167, 69)
        ));

        String[] cols = {"Mã", "Tên nhiệm vụ đang phụ trách", "Hạn chót", "Tiến độ"};
        modelHienTai = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableHienTai = new JTable(modelHienTai);
        setupTableStyle(tableHienTai);

        tableHienTai.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableHienTai.getColumnModel().getColumn(0).setMaxWidth(65);
        tableHienTai.getColumnModel().getColumn(1).setPreferredWidth(230);
        tableHienTai.getColumnModel().getColumn(2).setPreferredWidth(95);
        tableHienTai.getColumnModel().getColumn(3).setPreferredWidth(130);

        pnl.add(new JScrollPane(tableHienTai), BorderLayout.CENTER);

        // Nút bấm bên phải
        JPanel pnlBottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        JButton btnCapNhat = new JButton("Cập nhật tiến độ");
        btnCapNhat.setBackground(new Color(40, 167, 69));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCapNhat.setPreferredSize(new Dimension(190, 38));
        btnCapNhat.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCapNhat.addActionListener(e -> {
            int row = tableHienTai.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn 1 nhiệm vụ bên bảng hiện tại để cập nhật tiến độ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object idObj = tableHienTai.getValueAt(row, 0);
            if (idObj == null || idObj.toString().trim().equals("-")) {
                return;
            }

            int idNhiemVu = Integer.parseInt(idObj.toString().trim());
            String[] options = {"Đang chờ", "Đang làm", "Đã hoàn thành"};

            String selectedStatus = (String) JOptionPane.showInputDialog(
                    this,
                    "Chọn trạng thái tiến độ mới cho nhiệm vụ này:",
                    "Cập nhật tiến độ",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]
            );

            if (selectedStatus != null) {
                String updateSql = "UPDATE NhiemVuTruyenThong SET TrangThai = ? WHERE ID_NhiemVu = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setNString(1, selectedStatus);
                    ps.setInt(2, idNhiemVu);

                    int result = ps.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Cập nhật tiến độ thành công sang: [" + selectedStatus + "]!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Thông báo", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlBottomRight.add(btnCapNhat);
        pnl.add(pnlBottomRight, BorderLayout.SOUTH);

        return pnl;
    }

    private void setupTableStyle(JTable table) {
        table.setRowHeight(85);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        table.setShowGrid(true);
        table.setGridColor(new Color(210, 210, 210));

        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 240, 250));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadData() {
        modelMoi.setRowCount(0);
        modelHienTai.setRowCount(0);

        if (currentUser == null || currentUser.getMaSV() == null) {
            modelMoi.addRow(new Object[]{"-", "Không tìm thấy thông tin tài khoản", "-", "-"});
            modelHienTai.addRow(new Object[]{"-", "Không tìm thấy thông tin tài khoản", "-", "-"});
            return;
        }

        String sql = "SELECT ID_NhiemVu, TenNhiemVu, HanChot, TrangThai " +
                     "FROM NhiemVuTruyenThong " +
                     "WHERE LTRIM(RTRIM(MaSV_DuocGiao)) = LTRIM(RTRIM(?)) " +
                     "ORDER BY ID_NhiemVu DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentUser.getMaSV().trim());
            ResultSet rs = ps.executeQuery();

            boolean hasMoi = false;
            boolean hasHienTai = false;

            while (rs.next()) {
                int id = rs.getInt("ID_NhiemVu");
                String ten = rs.getString("TenNhiemVu");
                String hanChot = rs.getDate("HanChot") != null ? rs.getDate("HanChot").toString() : "Không thời hạn";
                String trangThai = rs.getString("TrangThai");

                // CỘT BÊN TRÁI: Việc mới chưa tiếp nhận
                if ("Mới phân công".equalsIgnoreCase(trangThai) || "Chờ tiếp nhận".equalsIgnoreCase(trangThai) || "Lên ý tưởng".equalsIgnoreCase(trangThai)) {
                    hasMoi = true;
                    String tagMoi = "<html><center><font size='4' color='#cc6600'><b>[Mới phân công]</b></font><br>"
                                  + "<font size='2.5' color='#666666'>Chờ tiếp nhận</font></center></html>";
                    modelMoi.addRow(new Object[]{
                            id,
                            "<html><left><b>" + ten + "</b></left></html>",
                            hanChot,
                            tagMoi
                    });
                } 
                // CỘT BÊN PHẢI: Việc đã nhận và đang theo dõi tiến độ
                else {
                    hasHienTai = true;
                    String tagTienDo;
                    if ("Đã hoàn thành".equalsIgnoreCase(trangThai)) {
                        tagTienDo = "<html><center><font size='4' color='#008000'><b>[Đã hoàn thành]</b></font><br>"
                                  + "<font size='2.5' color='#666666'>Đã hoàn tất 100%</font></center></html>";
                    } else if ("Đang chờ".equalsIgnoreCase(trangThai) || "Chờ duyệt".equalsIgnoreCase(trangThai)) {
                        tagTienDo = "<html><center><font size='4' color='#d97706'><b>[Đang chờ]</b></font><br>"
                                  + "<font size='2.5' color='#666666'>Chờ nghiệm thu</font></center></html>";
                    } else {
                        tagTienDo = "<html><center><font size='4' color='#0044cc'><b>[Đang làm]</b></font><br>"
                                  + "<font size='2.5' color='#666666'>Đang triển khai</font></center></html>";
                    }

                    modelHienTai.addRow(new Object[]{
                            id,
                            "<html><left><b>" + ten + "</b></left></html>",
                            hanChot,
                            tagTienDo
                    });
                }
            }

            if (!hasMoi) {
                modelMoi.addRow(new Object[]{"-", "Hiện không có nhiệm vụ mới nào", "-", "-"});
            }
            if (!hasHienTai) {
                modelHienTai.addRow(new Object[]{"-", "Chưa có nhiệm vụ nào đang thực hiện", "-", "-"});
            }

        } catch (Exception e) {
            e.printStackTrace();
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

            NhiemVuTruyenThongDialog dialog = new NhiemVuTruyenThongDialog(dummyFrame, dummyUser);
            dialog.setVisible(true);
            System.exit(0);
        });
    }
}