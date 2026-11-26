package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.dao.SuKienDAO;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.model.PhanQuyen;
import com.vanphongdoan.model.SuKien;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public class SuKienView extends JFrame {

    private NhanSu currentUser;
    private SuKienDAO suKienDAO = new SuKienDAO();
    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    // Form bên trái
    private JTextField txtMaSK, txtTenSK, txtNgayToChuc, txtDiaDiem, txtKinhPhi, txtViTriTuyen;
    private JComboBox<String> cbTrangThai;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi;

    // Bảng & tìm kiếm bên phải
    private JTextField txtTimKiem;
    private JComboBox<String> cbFilterTrangThai;
    private JTable table;
    private DefaultTableModel tableModel;

    private DecimalFormat df = new DecimalFormat("#,###");

    public SuKienView() {
        this(null);
    }

    public SuKienView(NhanSu user) {
        this.currentUser = user;

        // 1. Kiểm soát quyền bảo mật: Cho phép Ban Sự kiện (ID_Ban == 2) HOẶC được Admin cấp quyền
        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            boolean coQuyen = (currentUser.getIdBan() != null && currentUser.getIdBan() == 2);
            PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(currentUser.getTenDangNhap());
            if (pq != null && pq.isQuyenSuKien()) {
                coQuyen = true;
            }

            if (!coQuyen) {
                JOptionPane.showMessageDialog(this,
                        "Bạn không có quyền truy cập Quản lý Sự kiện!",
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

        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        setTitle("QUẢN LÝ SỰ KIỆN - HỆ THỐNG VĂN PHÒNG ĐOÀN EPU");
        setSize(1240, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setJMenuBar(createMenuBar());

        // 1. HEADER
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel lblTitle = new JLabel("QUẢN LÝ KẾ HOẠCH VÀ TỔ CHỨC SỰ KIỆN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        String tenUser = (currentUser != null && currentUser.getHoTen() != null)
                ? currentUser.getHoTen() : "Quản trị viên Văn phòng";
        JLabel lblUser = new JLabel("Xin chào: " + tenUser);
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // 2. PANEL FORM BÊN TRÁI (Giữ nguyên bố cục tối giản)
        JPanel panelLeft = new JPanel(new BorderLayout());
        panelLeft.setPreferredSize(new Dimension(340, 0));
        panelLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Thông tin sự kiện",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        JPanel panelFields = new JPanel(new GridBagLayout());
        panelFields.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaSK = new JTextField();
        txtMaSK.setEditable(false);
        txtMaSK.setBackground(new Color(235, 238, 242));
        txtMaSK.setToolTipText("Mã sự kiện tự động sinh");

        txtTenSK = new JTextField();
        txtNgayToChuc = new JTextField();
        txtNgayToChuc.setToolTipText("Nhập theo định dạng YYYY-MM-DD hoặc DD/MM/YYYY");

        txtDiaDiem = new JTextField();
        txtKinhPhi = new JTextField("0");
        cbTrangThai = new JComboBox<>(new String[]{"Sắp diễn ra", "Đang diễn ra", "Đã kết thúc", "Tạm hoãn"});

        addFormField(panelFields, "Mã sự kiện:", txtMaSK, gbc, 0);
        addFormField(panelFields, "Tên sự kiện:", txtTenSK, gbc, 1);
        addFormField(panelFields, "Ngày tổ chức:", txtNgayToChuc, gbc, 2);
        addFormField(panelFields, "Địa điểm:", txtDiaDiem, gbc, 3);
        addFormField(panelFields, "Kinh phí (VNĐ):", txtKinhPhi, gbc, 4);

        // Khởi tạo ô Vị trí tuyển kèm nút "Chọn..." tích chọn nhiều vị trí
        txtViTriTuyen = new JTextField("MC, Lễ tân, Chạy sự kiện, Hậu cần");
        txtViTriTuyen.setToolTipText("Bấm nút Chọn để tích nhiều vị trí hoặc gõ thêm trực tiếp");

        JButton btnChonViTri = new JButton("Chọn...");
        btnChonViTri.setMargin(new Insets(2, 8, 2, 8));
        btnChonViTri.setFocusPainted(false);

        JPopupMenu popupViTri = new JPopupMenu();
        String[] danhSachViTri = {
            "MC",
            "Lễ tân",
            "Chạy sự kiện",
            "Hậu cần",
            "Kỹ thuật / Âm thanh",
            "Truyền thông / Media",
            "An ninh / Điều phối"
        };
        java.util.List<JCheckBoxMenuItem> checkItems = new java.util.ArrayList<>();

        for (String vt : danhSachViTri) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(vt);
            item.putClientProperty("CheckBoxMenuItem.doNotCloseOnMouseClick", Boolean.TRUE);
            item.addActionListener(e -> {
                java.util.List<String> selected = new java.util.ArrayList<>();
                for (JCheckBoxMenuItem cb : checkItems) {
                    if (cb.isSelected()) {
                        selected.add(cb.getText());
                    }
                }
                txtViTriTuyen.setText(String.join(", ", selected));
            });
            checkItems.add(item);
            popupViTri.add(item);
        }

        btnChonViTri.addActionListener(e -> {
            String currentText = txtViTriTuyen.getText();
            for (JCheckBoxMenuItem cb : checkItems) {
                cb.setSelected(currentText.contains(cb.getText()));
            }
            popupViTri.show(btnChonViTri, 0, btnChonViTri.getHeight());
        });

        JPanel pnlViTriWrapper = new JPanel(new BorderLayout(4, 0));
        pnlViTriWrapper.add(txtViTriTuyen, BorderLayout.CENTER);
        pnlViTriWrapper.add(btnChonViTri, BorderLayout.EAST);

        addFormField(panelFields, "Vị trí tuyển:", pnlViTriWrapper, gbc, 5);
        addFormField(panelFields, "Trạng thái:", cbTrangThai, gbc, 6);

        panelLeft.add(panelFields, BorderLayout.NORTH);

        // Cụm 4 nút bấm phía dưới
        JPanel panelButtons = new JPanel(new GridLayout(2, 2, 10, 10));
        panelButtons.setBorder(new EmptyBorder(10, 12, 14, 12));

        btnThem = new JButton("Thêm mới");
        btnCapNhat = new JButton("Cập nhật");
        btnXoa = new JButton("Xóa bỏ");
        btnLamMoi = new JButton("Làm mới");

        Dimension btnDim = new Dimension(0, 34);
        btnThem.setPreferredSize(btnDim);
        btnCapNhat.setPreferredSize(btnDim);
        btnXoa.setPreferredSize(btnDim);
        btnLamMoi.setPreferredSize(btnDim);

        panelButtons.add(btnThem);
        panelButtons.add(btnCapNhat);
        panelButtons.add(btnXoa);
        panelButtons.add(btnLamMoi);

        panelLeft.add(panelButtons, BorderLayout.SOUTH);

        // 3. PANEL BẢNG BÊN PHẢI
        JPanel panelCenter = new JPanel(new BorderLayout(6, 8));
        panelCenter.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Danh sách sự kiện trong hệ thống",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)
        ));

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        txtTimKiem = new JTextField(16);
        txtTimKiem.setPreferredSize(new Dimension(180, 28));

        JButton btnTimKiem = new JButton("Tìm kiếm");
        cbFilterTrangThai = new JComboBox<>(new String[]{"-- Tất cả trạng thái --", "Sắp diễn ra", "Đang diễn ra", "Đã kết thúc", "Tạm hoãn"});
        cbFilterTrangThai.setPreferredSize(new Dimension(165, 28));

        panelSearch.add(new JLabel("Tìm kiếm:"));
        panelSearch.add(txtTimKiem);
        panelSearch.add(btnTimKiem);
        panelSearch.add(new JLabel("Lọc:"));
        panelSearch.add(cbFilterTrangThai);

        panelCenter.add(panelSearch, BorderLayout.NORTH);

        String[] columns = {"Mã SK", "Tên sự kiện", "Ngày tổ chức", "Địa điểm", "Kinh phí (VNĐ)", "Vị trí mở tuyển", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        table.getColumnModel().getColumn(0).setMaxWidth(65);
        table.getColumnModel().getColumn(1).setPreferredWidth(210);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(190);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        panelCenter.add(new JScrollPane(table), BorderLayout.CENTER);

        // Container chính
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(new EmptyBorder(0, 10, 10, 10));
        panelMain.add(panelLeft, BorderLayout.WEST);
        panelMain.add(panelCenter, BorderLayout.CENTER);
        add(panelMain, BorderLayout.CENTER);

        // SỰ KIỆN
        btnTimKiem.addActionListener(e -> loadDataToTable());
        txtTimKiem.addActionListener(e -> loadDataToTable());
        cbFilterTrangThai.addActionListener(e -> loadDataToTable());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaSK.setText(tableModel.getValueAt(row, 0).toString());
                    txtTenSK.setText(tableModel.getValueAt(row, 1).toString());
                    txtNgayToChuc.setText(tableModel.getValueAt(row, 2).toString());
                    txtDiaDiem.setText(tableModel.getValueAt(row, 3).toString());
                    txtKinhPhi.setText(tableModel.getValueAt(row, 4).toString().replace(",", "").replace(".", ""));
                    txtViTriTuyen.setText(tableModel.getValueAt(row, 5).toString());
                    cbTrangThai.setSelectedItem(tableModel.getValueAt(row, 6).toString());
                }
            }
        });

        btnThem.addActionListener(e -> xuLyThem());
        btnCapNhat.addActionListener(e -> xuLyCapNhat());
        btnXoa.addActionListener(e -> xuLyXoa());
        btnLamMoi.addActionListener(e -> clearForm());
    }

    private void addFormField(JPanel panel, String label, JComponent comp, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.65;
        comp.setPreferredSize(new Dimension(0, 28));
        panel.add(comp, gbc);
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        String kw = txtTimKiem.getText().trim().toLowerCase();
        String filterTT = cbFilterTrangThai.getSelectedIndex() > 0
                ? cbFilterTrangThai.getSelectedItem().toString() : "";

        List<SuKien> list = suKienDAO.getAllSuKien();
        if (list == null) return;

        for (SuKien sk : list) {
            if (!filterTT.isEmpty() && (sk.getTrangThai() == null || !sk.getTrangThai().equalsIgnoreCase(filterTT))) {
                continue;
            }
            if (!kw.isEmpty()) {
                String ten = sk.getTenSuKien() != null ? sk.getTenSuKien().toLowerCase() : "";
                String dd = sk.getDiaDiem() != null ? sk.getDiaDiem().toLowerCase() : "";
                if (!ten.contains(kw) && !dd.contains(kw)) continue;
            }

            String viTri = (sk.getViTriTuyen() != null) ? sk.getViTriTuyen() : "";

            tableModel.addRow(new Object[]{
                    sk.getIdSuKien(),
                    sk.getTenSuKien(),
                    sk.getNgayToChuc(),
                    sk.getDiaDiem(),
                    df.format(sk.getKinhPhi()),
                    viTri,
                    sk.getTrangThai()
            });
        }
    }

    private SuKien getFormData() {
        SuKien sk = new SuKien();
        if (!txtMaSK.getText().trim().isEmpty()) {
            sk.setIdSuKien(Integer.parseInt(txtMaSK.getText().trim()));
        }
        sk.setTenSuKien(txtTenSK.getText().trim());
        sk.setNgayToChuc(chuanHoaNgaySQL(txtNgayToChuc.getText()));
        sk.setDiaDiem(txtDiaDiem.getText().trim());
        try {
            String kp = txtKinhPhi.getText().trim().replace(",", "").replace(".", "");
            sk.setKinhPhi(kp.isEmpty() ? 0 : Double.parseDouble(kp));
        } catch (NumberFormatException e) {
            sk.setKinhPhi(0);
        }
        sk.setViTriTuyen(txtViTriTuyen.getText().trim());
        sk.setTrangThai(cbTrangThai.getSelectedItem().toString());
        return sk;
    }

    private void xuLyThem() {
        if (txtTenSK.getText().trim().isEmpty() || txtNgayToChuc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên sự kiện và Ngày tổ chức (YYYY-MM-DD)!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (suKienDAO.insertSuKien(getFormData())) {
            JOptionPane.showMessageDialog(this, "Thêm sự kiện thành công!");
            loadDataToTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại! Kiểm tra lại định dạng ngày (YYYY-MM-DD).", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyCapNhat() {
        if (txtMaSK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sự kiện cần cập nhật trên bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (suKienDAO.updateSuKien(getFormData())) {
            JOptionPane.showMessageDialog(this, "Cập nhật sự kiện thành công!");
            loadDataToTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyXoa() {
        if (txtMaSK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sự kiện cần xóa trên bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sự kiện này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtMaSK.getText().trim());
            if (suKienDAO.deleteSuKien(id)) {
                JOptionPane.showMessageDialog(this, "Đã xóa sự kiện!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtMaSK.setText("");
        txtTenSK.setText("");
        txtNgayToChuc.setText("");
        txtDiaDiem.setText("");
        txtKinhPhi.setText("0");
        txtViTriTuyen.setText("MC, Lễ tân, Chạy sự kiện, Hậu cần");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
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
        JMenuItem menuNhanSu = new JMenuItem("Quản lý Nhân sự");
        JMenuItem menuCaTruc = new JMenuItem("Quản lý Ca trực");
        JMenuItem menuSuKien = new JMenuItem("Quản lý Sự kiện");
        JMenuItem menuTruyenThong = new JMenuItem("Quản lý Truyền thông");

        menuNhanSu.addActionListener(e -> { new NhanSuView(currentUser).setVisible(true); this.dispose(); });
        menuCaTruc.addActionListener(e -> { new CaTrucView(currentUser).setVisible(true); this.dispose(); });
        menuSuKien.addActionListener(e -> loadDataToTable());
        menuTruyenThong.addActionListener(e -> { new TruyenThongView(currentUser).setVisible(true); this.dispose(); });

        menuChucNang.add(menuNhanSu);
        menuChucNang.add(menuCaTruc);
        menuChucNang.add(menuSuKien);
        menuChucNang.add(menuTruyenThong);

        // Phân quyền Menu Bar đồng bộ với NhanSuView và TruyenThongView
        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            Integer idBan = currentUser.getIdBan();
            boolean quyenCaTruc = (idBan != null && idBan == 1);
            boolean quyenSuKien = (idBan != null && idBan == 2);
            boolean quyenTruyenThong = (idBan != null && idBan == 3);

            PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(currentUser.getTenDangNhap());
            if (pq != null) {
                quyenCaTruc = quyenCaTruc || pq.isQuyenDoiNgoai();
                quyenSuKien = quyenSuKien || pq.isQuyenSuKien();
                quyenTruyenThong = quyenTruyenThong || pq.isQuyenTruyenThong();
            }

            menuCaTruc.setEnabled(quyenCaTruc);
            menuSuKien.setEnabled(quyenSuKien);
            menuTruyenThong.setEnabled(quyenTruyenThong);
        }

        menuBar.add(menuHeThong);
        menuBar.add(menuChucNang);
        return menuBar;
    }

    private String chuanHoaNgaySQL(String input) {
        if (input == null) return "";
        input = input.trim();
        if (input.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            String[] p = input.split("/");
            String day = p[0].length() == 1 ? "0" + p[0] : p[0];
            String month = p[1].length() == 1 ? "0" + p[1] : p[1];
            return p[2] + "-" + month + "-" + day;
        }
        return input;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuKienView(null).setVisible(true));
    }
}