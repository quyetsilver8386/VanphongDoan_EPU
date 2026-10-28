package com.vanphongdoan.view.admin;

import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;
import com.vanphongdoan.dao.SuKienDAO;
import com.vanphongdoan.model.SuKien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

public class SuKienView extends JFrame {

    private NhanSu currentUser;
    
    private SuKienDAO suKienDAO = new SuKienDAO();

    // Component Form thông tin bên trái
    private JTextField txtIdSuKien;
    private JTextField txtTenSuKien;
    private JTextField txtNgayToChuc;
    private JTextField txtDiaDiem;
    private JTextField txtKinhPhi;
    private JComboBox<String> cbTrangThai;

    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoi;

    // Component Danh sách bên phải
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
    
    // Biến cờ chống treo/lặp sự kiện
    private boolean isUpdatingSelection = false;

    public SuKienView() {
        this(null);
    }

    public SuKienView(NhanSu user) {
        this.currentUser = user;
        initComponents();
        loadDataFromDB();
    }

    private void initComponents() {
        setTitle("QUAN LY SU KIEN - HE THONG VAN PHONG DOAN EPU");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Gắn Menu Bar
        setJMenuBar(createMenuBar());

        // 2. Banner Header (Đồng bộ font & màu với NhanSuView)
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblTitle = new JLabel("QUẢN LÝ KẾ HOẠCH VÀ TỔ CHỨC SỰ KIỆN");
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

        // 3. Panel Body Trung Tâm
        JPanel panelCenter = new JPanel(new BorderLayout(12, 10));
        panelCenter.setBorder(new EmptyBorder(0, 10, 10, 10));

        panelCenter.add(createLeftPanel(), BorderLayout.WEST);
        panelCenter.add(createRightPanel(), BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);

        // Đăng ký sự kiện
        initEvents();
    }

    // Panel bên trái: Form nhập liệu được dựng bằng SpringLayout/Grid đồng bộ
    private JPanel createLeftPanel() {
        JPanel panelWest = new JPanel(new BorderLayout(5, 10));
        panelWest.setPreferredSize(new Dimension(320, 0));
        panelWest.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Thông tin sự kiện",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Khởi tạo các trường nhập liệu - KHÓA MÃ VÌ SQL TỰ TĂNG
        txtIdSuKien = new JTextField(15);
        txtIdSuKien.setPreferredSize(new Dimension(170, 26));
        txtIdSuKien.setEditable(false); // Khóa không cho nhập tay
        txtIdSuKien.setBackground(new Color(240, 240, 240)); // Màu xám nhạt thể hiện trường tự động
        txtIdSuKien.setToolTipText("Mã tự động tăng theo hệ thống");

        txtTenSuKien = new JTextField(15);
        txtTenSuKien.setPreferredSize(new Dimension(170, 26));

        txtNgayToChuc = new JTextField(15);
        txtNgayToChuc.setPreferredSize(new Dimension(170, 26));
        txtNgayToChuc.setToolTipText("Định dạng: YYYY-MM-DD");

        txtDiaDiem = new JTextField(15);
        txtDiaDiem.setPreferredSize(new Dimension(170, 26));

        txtKinhPhi = new JTextField(15);
        txtKinhPhi.setPreferredSize(new Dimension(170, 26));

        cbTrangThai = new JComboBox<>(new String[]{
                "Kế hoạch", 
                "Đang diễn ra", 
                "Đã hoàn thành", 
                "Tạm hoãn"
        });
        cbTrangThai.setPreferredSize(new Dimension(170, 26));

        // Hàng 0: Mã sự kiện
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        panelForm.add(new JLabel("Mã sự kiện:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panelForm.add(txtIdSuKien, gbc);

        // Hàng 1: Tên sự kiện
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        panelForm.add(new JLabel("Tên sự kiện:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panelForm.add(txtTenSuKien, gbc);

        // Hàng 2: Ngày tổ chức
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        panelForm.add(new JLabel("Ngày tổ chức:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panelForm.add(txtNgayToChuc, gbc);

        // Hàng 3: Địa điểm
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        panelForm.add(new JLabel("Địa điểm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panelForm.add(txtDiaDiem, gbc);

        // Hàng 4: Kinh phí
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.35;
        panelForm.add(new JLabel("Kinh phí:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panelForm.add(txtKinhPhi, gbc);

        // Hàng 5: Trạng thái
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.35;
        panelForm.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        panelForm.add(cbTrangThai, gbc);

        // Khoảng trống co giãn phía dưới
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.weighty = 1.0;
        panelForm.add(new JPanel(), gbc);

        panelWest.add(panelForm, BorderLayout.CENTER);

        // Panel nút bấm dưới cùng bên trái (Chuẩn 2x2 nút bo góc xám như NhanSuView)
        JPanel panelButtons = new JPanel(new GridLayout(2, 2, 8, 8));
        panelButtons.setBorder(new EmptyBorder(5, 10, 15, 10));

        btnThem = new JButton("Thêm mới");
        btnCapNhat = new JButton("Cập nhật");
        btnXoa = new JButton("Xóa bỏ");
        btnLamMoi = new JButton("Làm mới");

        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCapNhat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLamMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelButtons.add(btnThem);
        panelButtons.add(btnCapNhat);
        panelButtons.add(btnXoa);
        panelButtons.add(btnLamMoi);

        panelWest.add(panelButtons, BorderLayout.SOUTH);
        return panelWest;
    }

    // Panel bên phải: Bảng và thanh tìm kiếm
    private JPanel createRightPanel() {
        JPanel panelEast = new JPanel(new BorderLayout(8, 8));
        panelEast.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Danh sách sự kiện trong hệ thống",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        // Thanh tìm kiếm phía trên bảng
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        txtTimKiem = new JTextField(12);
        txtTimKiem.setPreferredSize(new Dimension(140, 28));

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cbFilterTrangThai = new JComboBox<>(new String[]{
                "-- Tất cả trạng thái --",
                "Kế hoạch",
                "Đang diễn ra",
                "Đã hoàn thành",
                "Tạm hoãn"
        });
        cbFilterTrangThai.setPreferredSize(new Dimension(150, 28));

        panelSearch.add(new JLabel("Tìm kiếm (Tên/Địa điểm):"));
        panelSearch.add(txtTimKiem);
        panelSearch.add(btnTimKiem);
        panelSearch.add(new JLabel("Lọc trạng thái:"));
        panelSearch.add(cbFilterTrangThai);

        panelEast.add(panelSearch, BorderLayout.NORTH);

        // Bảng dữ liệu JTable
        String[] columns = {"Mã SK", "Tên sự kiện", "Ngày tổ chức", "Địa điểm", "Kinh phí", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Căn giữa cho cột Mã, Ngày, Trạng thái
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(95);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(95);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        panelEast.add(scrollPane, BorderLayout.CENTER);

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

        menuSuKien.addActionListener(e -> clearForm());

        menuTruyenThong.addActionListener(e -> {
            new TruyenThongView(currentUser).setVisible(true);
            this.dispose();
        });

        menuChucNang.add(menuNhanSu);
        menuChucNang.add(menuCaTruc);
        menuChucNang.add(menuSuKien);
        menuChucNang.add(menuTruyenThong);

        menuBar.add(menuHeThong);
        menuBar.add(menuChucNang);

        return menuBar;
    }

    private void initEvents() {
        // CHỐNG TREO: Bọc kiểm tra getValueIsAdjusting và cờ isUpdatingSelection
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || isUpdatingSelection) return;

            int row = table.getSelectedRow();
            if (row != -1) {
                txtIdSuKien.setText(tableModel.getValueAt(row, 0) != null ? tableModel.getValueAt(row, 0).toString() : "");
                txtTenSuKien.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
                txtNgayToChuc.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                txtDiaDiem.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                txtKinhPhi.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
                if (tableModel.getValueAt(row, 5) != null) {
                    cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                }
            }
        });

        btnLamMoi.addActionListener(e -> clearForm());

        btnThem.addActionListener(e -> {
            String ten = txtTenSuKien.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên sự kiện!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtTenSuKien.requestFocus();
                return;
            }

            SuKien sk = new SuKien();
            sk.setTenSuKien(ten);
            sk.setNgayToChuc(txtNgayToChuc.getText().trim());
            sk.setDiaDiem(txtDiaDiem.getText().trim());
            try {
                sk.setKinhPhi(Double.parseDouble(txtKinhPhi.getText().trim().replaceAll("[^0-9]", "")));
            } catch (Exception ex) {
                sk.setKinhPhi(0);
            }
            sk.setTrangThai(cbTrangThai.getSelectedItem().toString());

            if (suKienDAO.insertSuKien(sk)) {
                JOptionPane.showMessageDialog(this, "Thêm sự kiện mới vào CSDL thành công!");
                loadDataFromDB();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm sự kiện thất bại! Kiểm tra định dạng ngày (YYYY-MM-DD).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCapNhat.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sự kiện cần cập nhật trên bảng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            isUpdatingSelection = true;
            tableModel.setValueAt(txtIdSuKien.getText().trim(), row, 0);
            tableModel.setValueAt(txtTenSuKien.getText().trim(), row, 1);
            tableModel.setValueAt(txtNgayToChuc.getText().trim(), row, 2);
            tableModel.setValueAt(txtDiaDiem.getText().trim(), row, 3);
            tableModel.setValueAt(txtKinhPhi.getText().trim(), row, 4);
            tableModel.setValueAt(cbTrangThai.getSelectedItem().toString(), row, 5);
            isUpdatingSelection = false;

            JOptionPane.showMessageDialog(this, "Cập nhật thông tin sự kiện thành công!");
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sự kiện cần xóa trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Bạn có chắc chắn muốn xóa sự kiện này khỏi cơ sở dữ liệu?", 
                    "Xác nhận xóa", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // 1. Lấy ID_SuKien từ cột 0 của dòng đang chọn
                    int idSuKien = Integer.parseInt(tableModel.getValueAt(row, 0).toString().trim());

                    // 2. Gọi DAO xóa trực tiếp trong SQL Server
                    if (suKienDAO.deleteSuKien(idSuKien)) {
                        JOptionPane.showMessageDialog(this, "Đã xóa sự kiện thành công!");
                        
                        // 3. Tải lại dữ liệu mới nhất từ CSDL lên bảng và xóa trắng form
                        loadDataFromDB();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa sự kiện thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Lỗi xử lý xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Tìm kiếm và lọc
        btnTimKiem.addActionListener(e -> locSuKien());
        txtTimKiem.addActionListener(e -> locSuKien());
        cbFilterTrangThai.addActionListener(e -> locSuKien());
    }

    private void clearForm() {
        txtIdSuKien.setText("");
        txtTenSuKien.setText("");
        txtNgayToChuc.setText("");
        txtDiaDiem.setText("");
        txtKinhPhi.setText("");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }
    
    private String formatTienTe(String rawMoney) {
        if (rawMoney == null || rawMoney.trim().isEmpty()) return "0";
        try {
            String clean = rawMoney.replaceAll("[^0-9]", "");
            if (clean.isEmpty()) return "0";
            long amount = Long.parseLong(clean);
            return String.format("%,d", amount); // Kết quả: 100,000,000
        } catch (Exception e) {
            return rawMoney;
        }
    }

    private void loadDataFromDB() {
        SwingWorker<List<SuKien>, Void> worker = new SwingWorker<List<SuKien>, Void>() {
            @Override
            protected List<SuKien> doInBackground() {
                return suKienDAO.getAllSuKien();
            }

            @Override
            protected void done() {
                try {
                    List<SuKien> list = get();
                    isUpdatingSelection = true;
                    tableModel.setRowCount(0);
                    for (SuKien sk : list) {
                        tableModel.addRow(new Object[]{
                                sk.getIdSuKien(),
                                sk.getTenSuKien(),
                                sk.getNgayToChuc(),
                                sk.getDiaDiem() != null ? sk.getDiaDiem() : "",
                                formatTienTe(String.valueOf((long) sk.getKinhPhi())),
                                sk.getTrangThai()
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

    private void locSuKien() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String selectedStatus = cbFilterTrangThai.getSelectedItem().toString();

        String[][] allData = {
                {"SK01", "Chào Tân Sinh Viên Khóa D19", "2026-11-05", "Hội trường A1", "15,000,000", "Đang diễn ra"},
                {"SK02", "Giải Bóng Đá Cúp Thanh Niên", "2026-11-12", "Sân bóng EPU", "8,500,000", "Kế hoạch"},
                {"SK03", "Tập huấn Cán bộ Đoàn Xuất sắc", "2026-11-20", "Phòng A806", "4,000,000", "Kế hoạch"},
                {"SK04", "Hiến Máu Nhân Đạo Giọt Hồng", "2026-10-15", "Sảnh nhà B", "5,000,000", "Đã hoàn thành"},
                {"SK05", "Hội thảo Chuyển đổi số Sinh viên", "2026-10-02", "Hội trường A1", "6,200,000", "Đã hoàn thành"}
        };

        isUpdatingSelection = true;
        tableModel.setRowCount(0);
        for (String[] row : allData) {
            boolean matchKeyword = keyword.isEmpty()
                    || row[1].toLowerCase().contains(keyword)
                    || row[3].toLowerCase().contains(keyword);

            boolean matchStatus = cbFilterTrangThai.getSelectedIndex() == 0
                    || row[5].equalsIgnoreCase(selectedStatus);

            if (matchKeyword && matchStatus) {
                tableModel.addRow(row);
            }
        }
        isUpdatingSelection = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SuKienView(null).setVisible(true);
        });
    }
}