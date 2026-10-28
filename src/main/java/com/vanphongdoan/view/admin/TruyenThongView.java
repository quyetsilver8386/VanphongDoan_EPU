package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.model.PhanQuyen;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TruyenThongView extends JFrame {

    private NhanSu currentUser;
    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    // Components bên trái
    private JTextField txtIdBaiViet;
    private JTextField txtTieuDe;
    private JTextField txtNguoiPhuTrach;
    private JTextField txtDeadline;
    private JComboBox<String> cbTrangThai;
    private JTextArea txtNoiDung;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    // Components bên phải
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JTable table;
    private DefaultTableModel tableModel;

    // Menu Bar
    private JMenuItem menuNhanSu;
    private JMenuItem menuCaTruc;
    private JMenuItem menuSuKien;
    private JMenuItem menuTruyenThong;

    public TruyenThongView(NhanSu user) {
        this.currentUser = user;

        // 1. Kiểm soát quyền bảo mật
        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(currentUser.getTenDangNhap());
            if (pq == null || !pq.isQuyenTruyenThong()) {
                JOptionPane.showMessageDialog(this, 
                        "Bạn không có quyền truy cập Quản lý Truyền thông!", 
                        "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
                new NhanSuView(currentUser).setVisible(true);
                this.dispose();
                return;
            }
        }

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("QUẢN LÝ TRUYỀN THÔNG - HỆ THỐNG VĂN PHÒNG ĐOÀN EPU");
        setSize(1100, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Khởi tạo Menu Bar
        setJMenuBar(createMenuBar());

        initUI();
        initEvents();
        loadMockData();
    }

    private void initUI() {
        // ================= 1. HEADER =================
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("QUẢN LÝ BÀI VIẾT & ẤN PHẨM TRUYỀN THÔNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        String hoTen = (currentUser != null) ? currentUser.getHoTen() : "Quản trị viên";
        JLabel lblUser = new JLabel("Xin chào: " + hoTen);
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // ================= 2. PANEL FORM TRÁI =================
        JPanel panelLeftContainer = new JPanel(new BorderLayout());
        panelLeftContainer.setBorder(new EmptyBorder(0, 10, 10, 5));
        panelLeftContainer.setPreferredSize(new Dimension(380, 0));

        JPanel panelLeft = new JPanel(new GridBagLayout());
        panelLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin nhiệm vụ truyền thông",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(0, 51, 102)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 12, 5, 12);
        gbc.weightx = 1.0;

        txtIdBaiViet = new JTextField();
        txtIdBaiViet.setEditable(false);
        txtIdBaiViet.setBackground(new Color(240, 240, 240));
        txtIdBaiViet.setPreferredSize(new Dimension(0, 28));

        txtTieuDe = new JTextField();
        txtTieuDe.setPreferredSize(new Dimension(0, 28));

        txtNguoiPhuTrach = new JTextField();
        txtNguoiPhuTrach.setPreferredSize(new Dimension(0, 28));

        txtDeadline = new JTextField();
        txtDeadline.setPreferredSize(new Dimension(0, 28));
        txtDeadline.setToolTipText("Định dạng: YYYY-MM-DD");

        cbTrangThai = new JComboBox<>(new String[]{"Lên ý tưởng", "Đang thiết kế/viết bài", "Chờ duyệt", "Đã đăng"});
        cbTrangThai.setPreferredSize(new Dimension(0, 28));

        txtNoiDung = new JTextArea(4, 20);
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        txtNoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane scrollNoiDung = new JScrollPane(txtNoiDung);

        int row = 0;
        // Mã bài viết
        gbc.gridx = 0; gbc.gridy = row++;
        panelLeft.add(new JLabel("Mã ấn phẩm/bài viết:"), gbc);
        gbc.gridy = row++;
        panelLeft.add(txtIdBaiViet, gbc);

        // Tiêu đề
        gbc.gridy = row++;
        panelLeft.add(new JLabel("Tiêu đề ấn phẩm / bài viết:"), gbc);
        gbc.gridy = row++;
        panelLeft.add(txtTieuDe, gbc);

        // Người phụ trách
        gbc.gridy = row++;
        panelLeft.add(new JLabel("Người phụ trách (Họ tên / MSSV):"), gbc);
        gbc.gridy = row++;
        panelLeft.add(txtNguoiPhuTrach, gbc);

        // Deadline
        gbc.gridy = row++;
        panelLeft.add(new JLabel("Thời hạn hoàn thành (YYYY-MM-DD):"), gbc);
        gbc.gridy = row++;
        panelLeft.add(txtDeadline, gbc);

        // Trạng thái
        gbc.gridy = row++;
        panelLeft.add(new JLabel("Trạng thái tiến độ:"), gbc);
        gbc.gridy = row++;
        panelLeft.add(cbTrangThai, gbc);

        // Ghi chú nội dung
        gbc.gridy = row++;
        panelLeft.add(new JLabel("Nội dung chi tiết / Ghi chú:"), gbc);
        gbc.gridy = row++;
        panelLeft.add(scrollNoiDung, gbc);

        // Cụm 4 nút bấm
        JPanel panelBtn = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBtn.setBorder(new EmptyBorder(10, 0, 5, 0));
        btnThem = new JButton("Thêm mới");
        btnSua = new JButton("Cập nhật");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSua.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLamMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelBtn.add(btnThem);
        panelBtn.add(btnSua);
        panelBtn.add(btnXoa);
        panelBtn.add(btnLamMoi);

        gbc.gridy = row++;
        panelLeft.add(panelBtn, gbc);

        panelLeftContainer.add(panelLeft, BorderLayout.CENTER);
        add(panelLeftContainer, BorderLayout.WEST);

        // ================= 3. BẢNG DỮ LIỆU PHẢI =================
        JPanel panelRight = new JPanel(new BorderLayout(5, 8));
        panelRight.setBorder(new EmptyBorder(0, 5, 10, 15));

        JPanel panelBorderRight = new JPanel(new BorderLayout(5, 8));
        panelBorderRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Danh sách ấn phẩm truyền thông trong hệ thống",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(0, 51, 102)));

        // Thanh tìm kiếm
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        pnlSearch.add(new JLabel("Tìm kiếm (Tiêu đề / Người phụ trách):"));
        txtTimKiem = new JTextField(24);
        txtTimKiem.setPreferredSize(new Dimension(240, 28));
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        panelBorderRight.add(pnlSearch, BorderLayout.NORTH);

        // Bảng JTable
        String[] columns = {"ID", "Tiêu đề ấn phẩm", "Người phụ trách", "Deadline", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 240, 250));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(160);

        // Căn giữa cột ID và Deadline
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        panelBorderRight.add(new JScrollPane(table), BorderLayout.CENTER);
        panelRight.add(panelBorderRight, BorderLayout.CENTER);
        add(panelRight, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuHeThong = new JMenu("Hệ thống");
        JMenuItem menuDangXuat = new JMenuItem("Đăng xuất");
        JMenuItem menuThoat = new JMenuItem("Thoát");
        menuDangXuat.addActionListener(e -> {
            new LoginView().setVisible(true);
            this.dispose();
        });
        menuThoat.addActionListener(e -> System.exit(0));
        menuHeThong.add(menuDangXuat);
        menuHeThong.addSeparator();
        menuHeThong.add(menuThoat);

        JMenu menuChucNang = new JMenu("Quản lý Chức năng");
        menuNhanSu = new JMenuItem("Quản lý Nhân sự");
        menuCaTruc = new JMenuItem("Quản lý Ca trực");
        menuSuKien = new JMenuItem("Quản lý Sự kiện");
        menuTruyenThong = new JMenuItem("Quản lý Truyền thông");

        menuNhanSu.addActionListener(e -> {
            new NhanSuView(currentUser).setVisible(true);
            this.dispose();
        });
        menuCaTruc.addActionListener(e -> {
            new CaTrucView(currentUser).setVisible(true);
            this.dispose();
        });
        menuSuKien.addActionListener(e -> {
            new SuKienView(currentUser).setVisible(true);
            this.dispose();
        });
        menuTruyenThong.addActionListener(e -> clearForm());

        menuChucNang.add(menuNhanSu);
        menuChucNang.add(menuCaTruc);
        menuChucNang.add(menuSuKien);
        menuChucNang.add(menuTruyenThong);

        // Phân quyền menu bar
        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(currentUser.getTenDangNhap());
            if (pq != null) {
                menuCaTruc.setEnabled(pq.isQuyenDoiNgoai());
                menuSuKien.setEnabled(pq.isQuyenSuKien());
                menuTruyenThong.setEnabled(pq.isQuyenTruyenThong());
            } else {
                menuCaTruc.setEnabled(false);
                menuSuKien.setEnabled(false);
                menuTruyenThong.setEnabled(false);
            }
        }

        menuBar.add(menuHeThong);
        menuBar.add(menuChucNang);
        return menuBar;
    }

    private void initEvents() {
        btnLamMoi.addActionListener(e -> clearForm());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtIdBaiViet.setText(table.getValueAt(row, 0).toString());
                    txtTieuDe.setText(table.getValueAt(row, 1).toString());
                    txtNguoiPhuTrach.setText(table.getValueAt(row, 2).toString());
                    txtDeadline.setText(table.getValueAt(row, 3).toString());
                    cbTrangThai.setSelectedItem(table.getValueAt(row, 4).toString());
                    txtNoiDung.setText(table.getValueAt(row, 5).toString());
                }
            }
        });

        btnThem.addActionListener(e -> {
            String tieuDe = txtTieuDe.getText().trim();
            if (tieuDe.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề bài viết!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtTieuDe.requestFocus();
                return;
            }
            int newId = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{
                    String.valueOf(newId),
                    tieuDe,
                    txtNguoiPhuTrach.getText().trim(),
                    txtDeadline.getText().trim(),
                    cbTrangThai.getSelectedItem().toString(),
                    txtNoiDung.getText().trim()
            });
            JOptionPane.showMessageDialog(this, "Thêm nhiệm vụ truyền thông thành công!");
            clearForm();
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            tableModel.setValueAt(txtTieuDe.getText().trim(), row, 1);
            tableModel.setValueAt(txtNguoiPhuTrach.getText().trim(), row, 2);
            tableModel.setValueAt(txtDeadline.getText().trim(), row, 3);
            tableModel.setValueAt(cbTrangThai.getSelectedItem().toString(), row, 4);
            tableModel.setValueAt(txtNoiDung.getText().trim(), row, 5);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bài viết này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(row);
                clearForm();
            }
        });

        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                loadMockData();
                return;
            }
            for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
                String tieuDe = tableModel.getValueAt(i, 1).toString().toLowerCase();
                String phuTrach = tableModel.getValueAt(i, 2).toString().toLowerCase();
                if (!tieuDe.contains(keyword) && !phuTrach.contains(keyword)) {
                    tableModel.removeRow(i);
                }
            }
        });
    }

    private void clearForm() {
        txtIdBaiViet.setText("");
        txtTieuDe.setText("");
        txtNguoiPhuTrach.setText("");
        txtDeadline.setText("");
        txtNoiDung.setText("");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void loadMockData() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"1", "Bài viết Chào Tân Sinh Viên D19", "Nguyễn Duy Bảo", "2026-11-05", "Đang thiết kế/viết bài", "Đăng fanpage Đoàn EPU"});
        tableModel.addRow(new Object[]{"2", "Infographic Ngày Pháp Luật VN", "Do Nguyen Quyet", "2026-11-09", "Chờ duyệt", "Kèm video recap ngắn"});
        tableModel.addRow(new Object[]{"3", "Minigame Tìm Hiểu Truyền Thống Đoàn", "Nguyễn Văn A", "2026-11-15", "Lên ý tưởng", "Chuẩn bị 5 phần quà"});
    }

    public TruyenThongView() {
        this(null);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TruyenThongView(null).setVisible(true);
        });
    }
}