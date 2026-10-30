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
import java.util.List;
import javax.swing.SwingWorker;

public class SuKienView extends JFrame {

    private NhanSu currentUser;
    private SuKienDAO suKienDAO = new SuKienDAO();

    // Component Form thông tin
    private JTextField txtIdSuKien;
    private JTextField txtTenSuKien;
    private JTextField txtNgayToChuc;
    private JTextField txtDiaDiem;
    private JTextField txtKinhPhi;
    private JTextField txtDanhSachViTri; // Ô BỔ SUNG: Nhập danh sách vị trí tuyển
    private JComboBox<String> cbTrangThai;

    private JButton btnThem;
    private JButton btnCapNhat;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JComboBox<String> cbFilterTrangThai;
    private JTable table;
    private DefaultTableModel tableModel;

    private JMenuItem menuNhanSu;
    private JMenuItem menuCaTruc;
    private JMenuItem menuSuKien;
    private JMenuItem menuTruyenThong;
    
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
        setTitle("QUẢN LÝ SỰ KIỆN - HỆ THỐNG VĂN PHÒNG ĐOÀN EPU");
        setSize(1080, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setJMenuBar(createMenuBar());

        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblTitle = new JLabel("QUẢN LÝ KẾ HOẠCH VÀ TỔ CHỨC SỰ KIỆN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        String hoTenUser = (currentUser != null && currentUser.getHoTen() != null) ? currentUser.getHoTen() : "Admin";
        JLabel lblUser = new JLabel("Xin chào: " + hoTenUser);
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout(12, 10));
        panelCenter.setBorder(new EmptyBorder(0, 10, 10, 10));

        panelCenter.add(createLeftPanel(), BorderLayout.WEST);
        panelCenter.add(createRightPanel(), BorderLayout.CENTER);

        add(panelCenter, BorderLayout.CENTER);

        initEvents();
    }

    private JPanel createLeftPanel() {
        JPanel panelWest = new JPanel(new BorderLayout(5, 10));
        panelWest.setPreferredSize(new Dimension(340, 0));
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
        gbc.insets = new Insets(5, 4, 5, 4);

        txtIdSuKien = new JTextField(15);
        txtIdSuKien.setPreferredSize(new Dimension(170, 26));
        txtIdSuKien.setEditable(false);
        txtIdSuKien.setBackground(new Color(240, 240, 240));

        txtTenSuKien = new JTextField(15);
        txtTenSuKien.setPreferredSize(new Dimension(170, 26));

        txtNgayToChuc = new JTextField(15);
        txtNgayToChuc.setPreferredSize(new Dimension(170, 26));

        txtDiaDiem = new JTextField(15);
        txtDiaDiem.setPreferredSize(new Dimension(170, 26));

        txtKinhPhi = new JTextField(15);
        txtKinhPhi.setPreferredSize(new Dimension(170, 26));

        txtDanhSachViTri = new JTextField(15);
        txtDanhSachViTri.setPreferredSize(new Dimension(170, 26));
        txtDanhSachViTri.setToolTipText("Nhập các vị trí phân cách bằng dấu phẩy. VD: MC, Lễ tân, Hậu cần");

        cbTrangThai = new JComboBox<>(new String[]{"Sắp diễn ra", "Đang diễn ra", "Đã hoàn thành", "Tạm hoãn"});
        cbTrangThai.setPreferredSize(new Dimension(170, 26));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35; panelForm.add(new JLabel("Mã sự kiện:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65; panelForm.add(txtIdSuKien, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35; panelForm.add(new JLabel("Tên sự kiện:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65; panelForm.add(txtTenSuKien, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35; panelForm.add(new JLabel("Ngày tổ chức:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65; panelForm.add(txtNgayToChuc, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35; panelForm.add(new JLabel("Địa điểm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65; panelForm.add(txtDiaDiem, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.35; panelForm.add(new JLabel("Kinh phí:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65; panelForm.add(txtKinhPhi, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.35; panelForm.add(new JLabel("Vị trí tuyển:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65; panelForm.add(txtDanhSachViTri, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.35; panelForm.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65; panelForm.add(cbTrangThai, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.weighty = 1.0;
        panelForm.add(new JPanel(), gbc);

        panelWest.add(panelForm, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new GridLayout(2, 2, 8, 8));
        panelButtons.setBorder(new EmptyBorder(5, 10, 15, 10));

        btnThem = new JButton("Thêm mới");
        btnCapNhat = new JButton("Cập nhật");
        btnXoa = new JButton("Xóa bỏ");
        btnLamMoi = new JButton("Làm mới");

        panelButtons.add(btnThem);
        panelButtons.add(btnCapNhat);
        panelButtons.add(btnXoa);
        panelButtons.add(btnLamMoi);

        panelWest.add(panelButtons, BorderLayout.SOUTH);
        return panelWest;
    }

    private JPanel createRightPanel() {
        JPanel panelEast = new JPanel(new BorderLayout(8, 8));
        panelEast.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Danh sách sự kiện trong hệ thống",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        txtTimKiem = new JTextField(12);
        btnTimKiem = new JButton("Tìm kiếm");
        cbFilterTrangThai = new JComboBox<>(new String[]{"-- Tất cả trạng thái --", "Sắp diễn ra", "Đang diễn ra", "Đã hoàn thành", "Tạm hoãn"});

        panelSearch.add(new JLabel("Tìm kiếm:"));
        panelSearch.add(txtTimKiem);
        panelSearch.add(btnTimKiem);
        panelSearch.add(new JLabel("Lọc:"));
        panelSearch.add(cbFilterTrangThai);

        panelEast.add(panelSearch, BorderLayout.NORTH);

        String[] columns = {"Mã SK", "Tên sự kiện", "Ngày tổ chức", "Địa điểm", "Vị trí mở tuyển", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        panelEast.add(scrollPane, BorderLayout.CENTER);

        return panelEast;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuHeThong = new JMenu("Hệ thống");
        JMenuItem menuDangXuat = new JMenuItem("Đăng xuất");
        JMenuItem menuThoat = new JMenuItem("Thoát");

        menuDangXuat.addActionListener(e -> { new LoginView().setVisible(true); this.dispose(); });
        menuThoat.addActionListener(e -> System.exit(0));
        menuHeThong.add(menuDangXuat); menuHeThong.addSeparator(); menuHeThong.add(menuThoat);

        JMenu menuChucNang = new JMenu("Quản lý Chức năng");
        menuNhanSu = new JMenuItem("Quản lý Nhân sự");
        menuCaTruc = new JMenuItem("Quản lý Ca trực");
        menuSuKien = new JMenuItem("Quản lý Sự kiện");
        menuTruyenThong = new JMenuItem("Quản lý Truyền thông");

        menuNhanSu.addActionListener(e -> { new NhanSuView(currentUser).setVisible(true); this.dispose(); });
        menuCaTruc.addActionListener(e -> { new CaTrucView(currentUser).setVisible(true); this.dispose(); });
        menuSuKien.addActionListener(e -> clearForm());
        menuTruyenThong.addActionListener(e -> { new TruyenThongView(currentUser).setVisible(true); this.dispose(); });

        menuChucNang.add(menuNhanSu); menuChucNang.add(menuCaTruc); menuChucNang.add(menuSuKien); menuChucNang.add(menuTruyenThong);
        menuBar.add(menuHeThong); menuBar.add(menuChucNang);
        return menuBar;
    }

    private void initEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || isUpdatingSelection) return;
            int row = table.getSelectedRow();
            if (row != -1) {
                txtIdSuKien.setText(tableModel.getValueAt(row, 0) != null ? tableModel.getValueAt(row, 0).toString() : "");
                txtTenSuKien.setText(tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString() : "");
                txtNgayToChuc.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
                txtDiaDiem.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                txtDanhSachViTri.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
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
                return;
            }

            SuKien sk = new SuKien();
            sk.setTenSuKien(ten);
            sk.setNgayToChuc(txtNgayToChuc.getText().trim());
            sk.setDiaDiem(txtDiaDiem.getText().trim());
            try { sk.setKinhPhi(Double.parseDouble(txtKinhPhi.getText().trim().replaceAll("[^0-9]", ""))); } catch (Exception ex) { sk.setKinhPhi(0); }
            sk.setTrangThai(cbTrangThai.getSelectedItem().toString());
            
            String viTri = txtDanhSachViTri.getText().trim();
            sk.setDanhSachViTri(viTri.isEmpty() ? "MC, Lễ tân, Chạy sự kiện, Hậu cần" : viTri);

            if (suKienDAO.insertSuKien(sk)) {
                JOptionPane.showMessageDialog(this, "Thêm sự kiện thành công!");
                loadDataFromDB();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm sự kiện thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCapNhat.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sự kiện cần cập nhật!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                SuKien sk = new SuKien();
                sk.setIdSuKien(Integer.parseInt(txtIdSuKien.getText().trim()));
                sk.setTenSuKien(txtTenSuKien.getText().trim());
                sk.setNgayToChuc(txtNgayToChuc.getText().trim());
                sk.setDiaDiem(txtDiaDiem.getText().trim());
                try { sk.setKinhPhi(Double.parseDouble(txtKinhPhi.getText().trim().replaceAll("[^0-9]", ""))); } catch (Exception ex) { sk.setKinhPhi(0); }
                sk.setTrangThai(cbTrangThai.getSelectedItem().toString());
                
                String viTri = txtDanhSachViTri.getText().trim();
                sk.setDanhSachViTri(viTri.isEmpty() ? "MC, Lễ tân, Chạy sự kiện, Hậu cần" : viTri);

                if (suKienDAO.updateSuKien(sk)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadDataFromDB();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sự kiện cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sự kiện này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int idSuKien = Integer.parseInt(tableModel.getValueAt(row, 0).toString().trim());
                if (suKienDAO.deleteSuKien(idSuKien)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadDataFromDB();
                    clearForm();
                }
            }
        });

        btnTimKiem.addActionListener(e -> loadDataFromDB());
    }

    private void clearForm() {
        txtIdSuKien.setText("");
        txtTenSuKien.setText("");
        txtNgayToChuc.setText("");
        txtDiaDiem.setText("");
        txtKinhPhi.setText("");
        txtDanhSachViTri.setText("MC, Lễ tân, Chạy sự kiện, Hậu cần");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
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
                                sk.getDanhSachViTri() != null ? sk.getDanhSachViTri() : "MC, Lễ tân, Chạy sự kiện, Hậu cần",
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuKienView(null).setVisible(true));
    }
}