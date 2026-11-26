package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.dao.TruyenThongDAO;
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
import java.util.ArrayList;
import java.util.List;

public class TruyenThongView extends JFrame {

    private NhanSu currentUser;
    private NhanSuDAO nhanSuDAO = new NhanSuDAO();
    private TruyenThongDAO truyenThongDAO = new TruyenThongDAO();

    private JTextField txtIdBaiViet;
    private JTextField txtTieuDe;
    private JComboBox<String> cbNguoiPhuTrach;
    private JTextField txtDeadline;
    private JComboBox<String> cbTrangThai;
    private JTextArea txtNoiDung;

    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;

    private JTextField txtTimKiem;
    private JComboBox<String> cbFilterTrangThai;
    private JButton btnTimKiem;
    private JTable table;
    private DefaultTableModel tableModel;

    private List<Object[]> danhSachAnPham = new ArrayList<>();

    private JMenuItem menuNhanSu;
    private JMenuItem menuCaTruc;
    private JMenuItem menuSuKien;
    private JMenuItem menuTruyenThong;

    public TruyenThongView() {
        this(null);
    }

    public TruyenThongView(NhanSu user) {
        this.currentUser = user;

        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            boolean coQuyen = (currentUser.getIdBan() != null && currentUser.getIdBan() == 3);
            PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(currentUser.getTenDangNhap());
            if (pq != null && pq.isQuyenTruyenThong()) {
                coQuyen = true;
            }

            if (!coQuyen) {
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
        setSize(1180, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setJMenuBar(createMenuBar());

        initUI();
        initEvents();
        loadDanhSachNguoiPhuTrach();
        loadDataFromDB();
    }

    private void initUI() {
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("QUẢN LÝ BÀI VIẾT & ẤN PHẨM TRUYỀN THÔNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        String hoTen = (currentUser != null && currentUser.getHoTen() != null)
                ? currentUser.getHoTen() : "Quản trị viên";
        JLabel lblUser = new JLabel("Xin chào: " + hoTen);
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        JPanel panelLeftContainer = new JPanel(new BorderLayout());
        panelLeftContainer.setBorder(new EmptyBorder(0, 10, 10, 5));
        panelLeftContainer.setPreferredSize(new Dimension(380, 0));

        JPanel panelLeft = new JPanel(new GridBagLayout());
        panelLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Thông tin nhiệm vụ truyền thông",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(0, 51, 102)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 12, 4, 12);
        gbc.weightx = 1.0;

        txtIdBaiViet = new JTextField();
        txtIdBaiViet.setEditable(false);
        txtIdBaiViet.setBackground(new Color(235, 238, 242));
        txtIdBaiViet.setPreferredSize(new Dimension(0, 28));
        txtIdBaiViet.setToolTipText("Mã ấn phẩm tự động tăng");

        txtTieuDe = new JTextField();
        txtTieuDe.setPreferredSize(new Dimension(0, 28));

        cbNguoiPhuTrach = new JComboBox<>();
        cbNguoiPhuTrach.setEditable(true);
        cbNguoiPhuTrach.setPreferredSize(new Dimension(0, 28));

        txtDeadline = new JTextField();
        txtDeadline.setPreferredSize(new Dimension(0, 28));
        txtDeadline.setToolTipText("Nhập định dạng YYYY-MM-DD hoặc DD/MM/YYYY");

        // Đồng bộ 4 trạng thái với NhiemVuTruyenThongDialog của Thành viên
        cbTrangThai = new JComboBox<>(new String[]{"Mới phân công", "Đang làm", "Đang chờ", "Đã hoàn thành"});
        cbTrangThai.setPreferredSize(new Dimension(0, 28));

        txtNoiDung = new JTextArea(4, 20);
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        txtNoiDung.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollNoiDung = new JScrollPane(txtNoiDung);
        scrollNoiDung.setPreferredSize(new Dimension(0, 85));
        scrollNoiDung.setMinimumSize(new Dimension(0, 85));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++;
        panelLeft.add(new JLabel("Mã ấn phẩm/bài viết:"), gbc);
        gbc.gridy = row++;
        panelLeft.add(txtIdBaiViet, gbc);

        gbc.gridy = row++;
        panelLeft.add(new JLabel("Tiêu đề ấn phẩm / bài viết (*):"), gbc);
        gbc.gridy = row++;
        panelLeft.add(txtTieuDe, gbc);

        gbc.gridy = row++;
        panelLeft.add(new JLabel("Người phụ trách:"), gbc);
        gbc.gridy = row++;
        panelLeft.add(cbNguoiPhuTrach, gbc);

        gbc.gridy = row++;
        panelLeft.add(new JLabel("Thời hạn hoàn thành (YYYY-MM-DD):"), gbc);
        gbc.gridy = row++;
        panelLeft.add(txtDeadline, gbc);

        gbc.gridy = row++;
        panelLeft.add(new JLabel("Trạng thái tiến độ:"), gbc);
        gbc.gridy = row++;
        panelLeft.add(cbTrangThai, gbc);

        gbc.gridy = row++;
        panelLeft.add(new JLabel("Nội dung chi tiết / Ghi chú:"), gbc);
        gbc.gridy = row++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panelLeft.add(scrollNoiDung, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel panelBtn = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBtn.setBorder(new EmptyBorder(8, 0, 5, 0));
        btnThem = new JButton("Thêm mới");
        btnSua = new JButton("Cập nhật");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        Dimension btnDim = new Dimension(0, 32);
        btnThem.setPreferredSize(btnDim);
        btnSua.setPreferredSize(btnDim);
        btnXoa.setPreferredSize(btnDim);
        btnLamMoi.setPreferredSize(btnDim);

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

        JPanel panelRight = new JPanel(new BorderLayout(5, 8));
        panelRight.setBorder(new EmptyBorder(0, 5, 10, 15));

        JPanel panelBorderRight = new JPanel(new BorderLayout(5, 8));
        panelBorderRight.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Danh sách ấn phẩm truyền thông trong hệ thống",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), new Color(0, 51, 102)));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        pnlSearch.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(16);
        txtTimKiem.setPreferredSize(new Dimension(170, 28));
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cbFilterTrangThai = new JComboBox<>(new String[]{
                "-- Tất cả trạng thái --", "Mới phân công", "Đang làm", "Đang chờ", "Đã hoàn thành"
        });
        cbFilterTrangThai.setPreferredSize(new Dimension(165, 28));

        pnlSearch.add(txtTimKiem);
        pnlSearch.add(btnTimKiem);
        pnlSearch.add(new JLabel("Tiến độ:"));
        pnlSearch.add(cbFilterTrangThai);
        panelBorderRight.add(pnlSearch, BorderLayout.NORTH);

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

        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(95);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(160);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        panelBorderRight.add(new JScrollPane(table), BorderLayout.CENTER);
        panelRight.add(panelBorderRight, BorderLayout.CENTER);
        add(panelRight, BorderLayout.CENTER);
    }

    private void loadDanhSachNguoiPhuTrach() {
        cbNguoiPhuTrach.removeAllItems();
        List<NhanSu> list = nhanSuDAO.getAllNhanSu();
        if (list != null) {
            for (NhanSu ns : list) {
                if (ns.getIdBan() != null && ns.getIdBan() == 3) {
                    cbNguoiPhuTrach.addItem(ns.getHoTen() + " - " + ns.getMaSV());
                }
            }
            for (NhanSu ns : list) {
                if (ns.getIdBan() == null || ns.getIdBan() != 3) {
                    cbNguoiPhuTrach.addItem(ns.getHoTen() + " - " + ns.getMaSV());
                }
            }
        }
    }

    private String chuanHoaNgay(String input) {
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
        menuTruyenThong.addActionListener(e -> {
            loadDataFromDB();
            clearForm();
        });

        menuChucNang.add(menuNhanSu);
        menuChucNang.add(menuCaTruc);
        menuChucNang.add(menuSuKien);
        menuChucNang.add(menuTruyenThong);

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

    private void initEvents() {
        btnLamMoi.addActionListener(e -> {
            loadDataFromDB();
            clearForm();
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    txtIdBaiViet.setText(table.getValueAt(row, 0).toString());
                    txtTieuDe.setText(table.getValueAt(row, 1).toString());
                    cbNguoiPhuTrach.setSelectedItem(table.getValueAt(row, 2).toString());
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
            String phuTrach = cbNguoiPhuTrach.getEditor().getItem() != null
                    ? cbNguoiPhuTrach.getEditor().getItem().toString().trim() : "";
            String deadline = chuanHoaNgay(txtDeadline.getText());
            String trangThai = cbTrangThai.getSelectedItem().toString();
            String moTa = txtNoiDung.getText().trim();

            if (truyenThongDAO.insert(tieuDe, phuTrach, deadline, trangThai, moTa)) {
                loadDataFromDB();
                JOptionPane.showMessageDialog(this, "Thêm nhiệm vụ truyền thông vào CSDL thành công!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại! Kiểm tra định dạng ngày (YYYY-MM-DD).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnSua.addActionListener(e -> {
            String idCanSua = txtIdBaiViet.getText().trim();
            if (idCanSua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(idCanSua);
            String tieuDe = txtTieuDe.getText().trim();
            String phuTrach = cbNguoiPhuTrach.getEditor().getItem() != null
                    ? cbNguoiPhuTrach.getEditor().getItem().toString().trim() : "";
            String deadline = chuanHoaNgay(txtDeadline.getText());
            String trangThai = cbTrangThai.getSelectedItem().toString();
            String moTa = txtNoiDung.getText().trim();

            if (truyenThongDAO.update(id, tieuDe, phuTrach, deadline, trangThai, moTa)) {
                loadDataFromDB();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnXoa.addActionListener(e -> {
            String idCanXoa = txtIdBaiViet.getText().trim();
            if (idCanXoa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhiệm vụ này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(idCanXoa);
                if (truyenThongDAO.delete(id)) {
                    loadDataFromDB();
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Đã xóa nhiệm vụ!");
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnTimKiem.addActionListener(e -> refreshTable());
        txtTimKiem.addActionListener(e -> refreshTable());
        cbFilterTrangThai.addActionListener(e -> refreshTable());
    }

    private void clearForm() {
        txtIdBaiViet.setText("");
        txtTieuDe.setText("");
        if (cbNguoiPhuTrach.getItemCount() > 0) {
            cbNguoiPhuTrach.setSelectedIndex(0);
        } else {
            cbNguoiPhuTrach.setSelectedItem("");
        }
        txtDeadline.setText("");
        txtNoiDung.setText("");
        cbTrangThai.setSelectedIndex(0);
        table.clearSelection();
    }

    private void loadDataFromDB() {
        danhSachAnPham = truyenThongDAO.getAll();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        String filterTT = cbFilterTrangThai.getSelectedIndex() > 0
                ? cbFilterTrangThai.getSelectedItem().toString() : "";

        for (Object[] r : danhSachAnPham) {
            String tieuDe = r[1].toString().toLowerCase();
            String phuTrach = r[2].toString().toLowerCase();
            String trangThai = r[4].toString();

            if (!filterTT.isEmpty() && !trangThai.equalsIgnoreCase(filterTT)) {
                continue;
            }
            if (!keyword.isEmpty() && !tieuDe.contains(keyword) && !phuTrach.contains(keyword)) {
                continue;
            }
            tableModel.addRow(r);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TruyenThongView(null).setVisible(true));
    }
}   