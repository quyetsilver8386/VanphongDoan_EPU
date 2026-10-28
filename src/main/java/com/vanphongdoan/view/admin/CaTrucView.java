package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.CaTrucDAO;
import com.vanphongdoan.model.CaTruc;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CaTrucView extends JFrame {

    private NhanSu currentUser;
    private CaTrucDAO caTrucDAO = new CaTrucDAO();

    // Form Components
    private JTextField txtIdCa;
    private JComboBox<String> cbBuoiTruc;
    private JTextField txtNgayTruc;
    private JTextField txtThu;
    private JTextField txtMaSV;
    private JTextField txtHoTen;
    private JComboBox<String> cbTrangThai;
    private JTextField txtGhiChu;

    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnTaoLichTuan; // <-- Thêm dòng này

    // Table & Filter Components
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JComboBox<String> cbFilterTrangThai;
    private JTable table;
    private DefaultTableModel tableModel;

    // Menu Bar
    private JMenuItem menuNhanSu;
    private JMenuItem menuCaTruc;
    private JMenuItem menuSuKien;
    private JMenuItem menuTruyenThong;

    private boolean isUpdatingSelection = false;

    public CaTrucView() {
        this(null);
    }

    public CaTrucView(NhanSu user) {
        this.currentUser = user;
        initComponents();
        loadDataFromDB();
    }

    private void initComponents() {
        setTitle("QUẢN LÝ CA TRỰC - HỆ THỐNG VĂN PHÒNG ĐOÀN EPU");
        setSize(1050, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setJMenuBar(createMenuBar());

        // Header Panel đồng bộ
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblTitle = new JLabel("QUẢN LÝ LỊCH TRỰC VĂN PHÒNG ĐOÀN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        String hoTenUser = (currentUser != null && currentUser.getHoTen() != null)
                ? currentUser.getHoTen() : "Admin";
        JLabel lblUser = new JLabel("Xin chào: " + hoTenUser);
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // Center Panel
        JPanel panelCenter = new JPanel(new BorderLayout(12, 10));
        panelCenter.setBorder(new EmptyBorder(0, 10, 10, 10));

        panelCenter.add(createLeftPanel(), BorderLayout.WEST);
        panelCenter.add(createRightPanel(), BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);

        initEvents();
    }

    private JPanel createLeftPanel() {
        JPanel panelWest = new JPanel(new BorderLayout(5, 10));
        panelWest.setPreferredSize(new Dimension(330, 0));
        panelWest.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Thông tin ca trực",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(new EmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);

        txtIdCa = new JTextField(15);
        txtIdCa.setPreferredSize(new Dimension(170, 26));
        txtIdCa.setEditable(false);
        txtIdCa.setBackground(new Color(240, 240, 240));

        // CHỈ 2 CA: Sáng và Chiều khớp ràng buộc SQL
        cbBuoiTruc = new JComboBox<>(new String[]{"Sáng", "Chiều"});
        cbBuoiTruc.setPreferredSize(new Dimension(170, 26));

        txtNgayTruc = new JTextField(15);
        txtNgayTruc.setPreferredSize(new Dimension(170, 26));
        txtNgayTruc.setToolTipText("Định dạng: YYYY-MM-DD");

        txtThu = new JTextField(15);
        txtThu.setPreferredSize(new Dimension(170, 26));

        txtMaSV = new JTextField(15);
        txtMaSV.setPreferredSize(new Dimension(170, 26));

        txtHoTen = new JTextField(15);
        txtHoTen.setPreferredSize(new Dimension(170, 26));

        cbTrangThai = new JComboBox<>(new String[]{
                "Mở đăng ký",
                "Đã phân công",
                "Không trực"
        });
        cbTrangThai.setPreferredSize(new Dimension(170, 26));

        // Khóa ô nhập sinh viên khi ca là "Không trực"
        cbTrangThai.addActionListener(e -> {
            boolean isKhongTruc = "Không trực".equals(cbTrangThai.getSelectedItem());
            if (isKhongTruc) {
                txtMaSV.setText("");
                txtHoTen.setText("");
                txtMaSV.setEnabled(false);
                txtHoTen.setEnabled(false);
                txtMaSV.setBackground(new Color(240, 240, 240));
                txtHoTen.setBackground(new Color(240, 240, 240));
            } else {
                txtMaSV.setEnabled(true);
                txtHoTen.setEnabled(true);
                txtMaSV.setBackground(Color.WHITE);
                txtHoTen.setBackground(Color.WHITE);
            }
        });

        txtGhiChu = new JTextField(15);
        txtGhiChu.setPreferredSize(new Dimension(170, 26));

        int y = 0;
        addFormField(panelForm, gbc, "Mã ca trực:", txtIdCa, y++);
        addFormField(panelForm, gbc, "Buổi trực:", cbBuoiTruc, y++);
        addFormField(panelForm, gbc, "Ngày trực (YYYY-MM-DD):", txtNgayTruc, y++);
        addFormField(panelForm, gbc, "Thứ trong tuần:", txtThu, y++);
        addFormField(panelForm, gbc, "Mã SV trực:", txtMaSV, y++);
        addFormField(panelForm, gbc, "Họ tên SV:", txtHoTen, y++);
        addFormField(panelForm, gbc, "Trạng thái:", cbTrangThai, y++);
        addFormField(panelForm, gbc, "Ghi chú:", txtGhiChu, y++);

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2; gbc.weighty = 1.0;
        panelForm.add(new JPanel(), gbc);

        panelWest.add(panelForm, BorderLayout.CENTER);

        // Buttons
        JPanel panelButtons = new JPanel(new GridLayout(3, 2, 8, 8)); // Đổi thành 3 hàng x 2 cột
        panelButtons.setBorder(new EmptyBorder(5, 10, 15, 10));

        btnThem = new JButton("Thêm mới");
        btnCapNhat = new JButton("Cập nhật");
        btnXoa = new JButton("Xóa ca");
        btnLamMoi = new JButton("Làm mới");

        btnTaoLichTuan = new JButton("Tạo lịch tuần");
        btnTaoLichTuan.setBackground(new Color(40, 167, 69));
        btnTaoLichTuan.setForeground(Color.WHITE);
        btnTaoLichTuan.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnDongDangKy = new JButton("Khóa ca còn lại");
        btnDongDangKy.setBackground(new Color(220, 53, 69));
        btnDongDangKy.setForeground(Color.WHITE);

        panelButtons.add(btnThem);
        panelButtons.add(btnCapNhat);
        panelButtons.add(btnXoa);
        panelButtons.add(btnLamMoi);
        panelButtons.add(btnTaoLichTuan);
        panelButtons.add(btnDongDangKy);

        panelWest.add(panelButtons, BorderLayout.SOUTH);
        return panelWest;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent comp, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.35;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }

    private JPanel createRightPanel() {
        JPanel panelEast = new JPanel(new BorderLayout(8, 8));
        panelEast.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Danh sách ca trực văn phòng",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        txtTimKiem = new JTextField(12);
        txtTimKiem.setPreferredSize(new Dimension(140, 28));

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cbFilterTrangThai = new JComboBox<>(new String[]{
                "-- Tất cả trạng thái --",
                "Mở đăng ký",
                "Đã phân công",
                "Không trực"
        });
        cbFilterTrangThai.setPreferredSize(new Dimension(150, 28));

        panelSearch.add(new JLabel("Tìm kiếm (Mã SV/Ngày):"));
        panelSearch.add(txtTimKiem);
        panelSearch.add(btnTimKiem);
        panelSearch.add(new JLabel("Lọc trạng thái:"));
        panelSearch.add(cbFilterTrangThai);

        panelEast.add(panelSearch, BorderLayout.NORTH);

        String[] columns = {"Mã Ca", "Buổi trực", "Ngày trực", "Thứ", "Mã SV", "Họ tên SV", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(140);

        panelEast.add(new JScrollPane(table), BorderLayout.CENTER);
        return panelEast;
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

        JMenu menuQuanLy = new JMenu("Quản lý Chức năng");
        menuNhanSu = new JMenuItem("Quản lý Nhân sự");
        menuCaTruc = new JMenuItem("Quản lý Ca trực");
        menuSuKien = new JMenuItem("Quản lý Sự kiện");
        menuTruyenThong = new JMenuItem("Quản lý Truyền thông");

        menuNhanSu.addActionListener(e -> {
            this.dispose();
            new NhanSuView(currentUser).setVisible(true);
        });

        menuCaTruc.addActionListener(e -> clearForm());

        menuSuKien.addActionListener(e -> {
            this.dispose();
            new SuKienView(currentUser).setVisible(true);
        });

        menuTruyenThong.addActionListener(e -> {
            this.dispose();
            new TruyenThongView(currentUser).setVisible(true);
        });

        menuQuanLy.add(menuNhanSu);
        menuQuanLy.add(menuCaTruc);
        menuQuanLy.add(menuSuKien);
        menuQuanLy.add(menuTruyenThong);

        menuBar.add(menuHeThong);
        menuBar.add(menuQuanLy);
        return menuBar;
    }

    private void initEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || isUpdatingSelection) return;
            int row = table.getSelectedRow();
            if (row != -1) {
                txtIdCa.setText(getValueOrEmpty(row, 0));
                cbBuoiTruc.setSelectedItem(getValueOrEmpty(row, 1));
                txtNgayTruc.setText(getValueOrEmpty(row, 2));
                txtThu.setText(getValueOrEmpty(row, 3));
                txtMaSV.setText(getValueOrEmpty(row, 4));
                txtHoTen.setText(getValueOrEmpty(row, 5));
                cbTrangThai.setSelectedItem(getValueOrEmpty(row, 6));
                txtGhiChu.setText(getValueOrEmpty(row, 7));
            }
        });

        btnLamMoi.addActionListener(e -> clearForm());

        btnThem.addActionListener(e -> {
            String ngay = txtNgayTruc.getText().trim();
            if (ngay.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Ngày trực (YYYY-MM-DD)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CaTruc ct = new CaTruc();
            ct.setBuoiTruc(cbBuoiTruc.getSelectedItem().toString());
            ct.setNgayTruc(ngay);
            ct.setThu(txtThu.getText().trim());
            ct.setTrangThai(cbTrangThai.getSelectedItem().toString());
            ct.setGhiChu(txtGhiChu.getText().trim());

            if ("Không trực".equals(ct.getTrangThai())) {
                ct.setMaSV(null);
                ct.setHoTen(null);
            } else {
                ct.setMaSV(txtMaSV.getText().trim());
                ct.setHoTen(txtHoTen.getText().trim());
            }

            if (caTrucDAO.insert(ct)) {
                JOptionPane.showMessageDialog(this, "Thêm ca trực mới thành công!");
                loadDataFromDB();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại! Vui lòng kiểm tra lại ngày trực.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCapNhat.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ca trực cần cập nhật!");
                return;
            }
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

            CaTruc ct = new CaTruc();
            ct.setIdCa(id);
            ct.setBuoiTruc(cbBuoiTruc.getSelectedItem().toString());
            ct.setNgayTruc(txtNgayTruc.getText().trim());
            ct.setThu(txtThu.getText().trim());
            ct.setTrangThai(cbTrangThai.getSelectedItem().toString());
            ct.setGhiChu(txtGhiChu.getText().trim());

            if ("Không trực".equals(ct.getTrangThai())) {
                ct.setMaSV(null);
                ct.setHoTen(null);
            } else {
                ct.setMaSV(txtMaSV.getText().trim());
                ct.setHoTen(txtHoTen.getText().trim());
            }

            if (caTrucDAO.update(ct)) {
                JOptionPane.showMessageDialog(this, "Cập nhật ca trực thành công!");
                loadDataFromDB();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ca trực cần xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa ca trực này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                if (caTrucDAO.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa ca trực!");
                    loadDataFromDB();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa ca trực thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnTaoLichTuan.addActionListener(e -> {
            // Mặc định lấy ngày Thứ Hai của tuần kế tiếp
            java.time.LocalDate nextMonday = java.time.LocalDate.now()
                    .with(java.time.DayOfWeek.MONDAY)
                    .plusWeeks(1);

            String input = JOptionPane.showInputDialog(
                    this,
                    "Nhập ngày Thứ Hai bắt đầu tuần trực (YYYY-MM-DD):",
                    nextMonday.toString()
            );

            if (input != null && !input.trim().isEmpty()) {
                try {
                    java.time.LocalDate ngayThu2 = java.time.LocalDate.parse(input.trim());
                    if (caTrucDAO.taoLichTrucTuan(ngayThu2)) {
                        JOptionPane.showMessageDialog(this, 
                                "Đã tạo thành công danh sách ca trực tuần (10 ca từ Thứ 2 đến Thứ 6) ở trạng thái 'Mở đăng ký'!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadDataFromDB();
                    } else {
                        JOptionPane.showMessageDialog(this, "Tạo lịch tuần thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng nhập đúng chuẩn YYYY-MM-DD.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnTimKiem.addActionListener(e -> locCaTruc());
        txtTimKiem.addActionListener(e -> locCaTruc());
        cbFilterTrangThai.addActionListener(e -> locCaTruc());
    }

    private String getValueOrEmpty(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    private void clearForm() {
        txtIdCa.setText("");
        cbBuoiTruc.setSelectedIndex(0);
        txtNgayTruc.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtThu.setText("");
        txtMaSV.setText("");
        txtHoTen.setText("");
        cbTrangThai.setSelectedIndex(0);
        txtGhiChu.setText("");
        table.clearSelection();
    }

    private void loadDataFromDB() {
        // Chạy truy vấn SQL ở luồng phụ ngầm (Background Thread)
        SwingWorker<List<CaTruc>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CaTruc> doInBackground() {
                return caTrucDAO.getAllCaTruc();
            }

            @Override
            protected void done() {
                try {
                    List<CaTruc> list = get();
                    isUpdatingSelection = true;
                    tableModel.setRowCount(0);
                    for (CaTruc ct : list) {
                        tableModel.addRow(new Object[]{
                                ct.getIdCa(),
                                ct.getBuoiTruc(),
                                ct.getNgayTruc(),
                                ct.getThu() != null ? ct.getThu() : "",
                                ct.getMaSV() != null ? ct.getMaSV() : "",
                                ct.getHoTen() != null ? ct.getHoTen() : "",
                                ct.getTrangThai(),
                                ct.getGhiChu() != null ? ct.getGhiChu() : ""
                        });
                    }
                    isUpdatingSelection = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void locCaTruc() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String selectedStatus = cbFilterTrangThai.getSelectedItem().toString();

        isUpdatingSelection = true;
        tableModel.setRowCount(0);
        List<CaTruc> list = caTrucDAO.getAllCaTruc();
        for (CaTruc ct : list) {
            String ma = ct.getMaSV() != null ? ct.getMaSV().toLowerCase() : "";
            String ngay = ct.getNgayTruc() != null ? ct.getNgayTruc().toLowerCase() : "";
            String hoTen = ct.getHoTen() != null ? ct.getHoTen().toLowerCase() : "";

            boolean matchKeyword = keyword.isEmpty() || ma.contains(keyword) || ngay.contains(keyword) || hoTen.contains(keyword);
            boolean matchStatus = cbFilterTrangThai.getSelectedIndex() == 0 || ct.getTrangThai().equalsIgnoreCase(selectedStatus);

            if (matchKeyword && matchStatus) {
                tableModel.addRow(new Object[]{
                        ct.getIdCa(),
                        ct.getBuoiTruc(),
                        ct.getNgayTruc(),
                        ct.getThu() != null ? ct.getThu() : "",
                        ct.getMaSV() != null ? ct.getMaSV() : "",
                        ct.getHoTen() != null ? ct.getHoTen() : "",
                        ct.getTrangThai(),
                        ct.getGhiChu() != null ? ct.getGhiChu() : ""
                });
            }
        }
        isUpdatingSelection = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CaTrucView(null).setVisible(true));
    }
}