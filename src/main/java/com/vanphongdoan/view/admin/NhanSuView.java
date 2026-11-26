package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NhanSuView extends JFrame {

    private NhanSu currentUser;
    private JTable table;
    private DefaultTableModel tableModel;

    // Các ô nhập liệu - Tài khoản và Hồ sơ
    private JTextField txtTenDangNhap, txtMaSV, txtHoTen, txtKhoa, txtLop, txtSdt, txtTimKiem;
    private JComboBox<String> cbVaiTro, cbBan;
    private JCheckBox chkTrangThai;

    // Các component tìm kiếm & bộ lọc
    private JComboBox<String> cbFilterBan;
    private JComboBox<String> cbFilterKhoa;

    // Các nút bấm chức năng chuẩn nghiệp vụ
    private JButton btnThem, btnSua, btnKhoaMoKhoa, btnResetMK, btnLamMoi, btnTimKiem, btnPhanQuyen;

    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    public NhanSuView(NhanSu ns) {
        this.currentUser = ns;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("QUAN LY NHAN SU - HE THONG VAN PHONG DOAN EPU");
        setSize(1120, 670);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Chèn thanh Menu Bar
        setJMenuBar(createMenuBar());

        // 1. PANEL HEADER
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("QUẢN TRỊ TÀI KHOẢN VÀ DANH SÁCH NHÂN SỰ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        String tenHienThi = (currentUser != null && currentUser.getHoTen() != null)
                ? currentUser.getHoTen() : "Quản trị viên Văn phòng";
        JLabel lblUser = new JLabel("Xin chào: " + tenHienThi);
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // 2. PANEL FORM BÊN TRÁI
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản & nhân sự"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTenDangNhap = new JTextField(15);
        txtMaSV = new JTextField(15);
        txtHoTen = new JTextField(15);
        txtKhoa = new JTextField(15);
        txtLop = new JTextField(15);
        txtSdt = new JTextField(15);

        cbVaiTro = new JComboBox<>(new String[]{"Thành viên", "Ban Điều hành", "Quản trị viên"});
        cbBan = new JComboBox<>(new String[]{"Không thuộc ban", "Ban Đối ngoại", "Ban Sự kiện", "Ban Truyền thông"});
        chkTrangThai = new JCheckBox("Đang hoạt động", true);

        addFormField(panelForm, "Tên đăng nhập:", txtTenDangNhap, gbc, 0);
        addFormField(panelForm, "Mã SV / Cán bộ:", txtMaSV, gbc, 1);
        addFormField(panelForm, "Họ và tên:", txtHoTen, gbc, 2);
        addFormField(panelForm, "Khóa sinh viên:", txtKhoa, gbc, 3);
        addFormField(panelForm, "Lớp / Chi đoàn:", txtLop, gbc, 4);
        addFormField(panelForm, "Số điện thoại:", txtSdt, gbc, 5);
        addFormField(panelForm, "Vai trò:", cbVaiTro, gbc, 6);
        addFormField(panelForm, "Ban chuyên môn:", cbBan, gbc, 7);

        gbc.gridx = 1;
        gbc.gridy = 8;
        panelForm.add(chkTrangThai, gbc);

        JPanel panelButtons = new JPanel(new GridLayout(3, 2, 6, 6));
        btnThem = new JButton("Thêm mới");
        btnSua = new JButton("Cập nhật");
        btnKhoaMoKhoa = new JButton("Khóa / Mở");
        btnResetMK = new JButton("Reset MK");
        btnPhanQuyen = new JButton("Phân quyền BĐH");
        btnLamMoi = new JButton("Làm mới");

        panelButtons.add(btnThem);
        panelButtons.add(btnSua);
        panelButtons.add(btnKhoaMoKhoa);
        panelButtons.add(btnResetMK);
        panelButtons.add(btnPhanQuyen);
        panelButtons.add(btnLamMoi);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        panelForm.add(panelButtons, gbc);

        add(panelForm, BorderLayout.WEST);

        // 3. PANEL CENTER BÊN PHẢI
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Danh sách nhân sự trong hệ thống"));

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        txtTimKiem = new JTextField(12);
        txtTimKiem.setPreferredSize(new Dimension(140, 28));

        btnTimKiem = new JButton("Tìm");
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cbFilterBan = new JComboBox<>(new String[]{
                "-- Tất cả ban --",
                "Ban Đối ngoại",
                "Ban Sự kiện",
                "Ban Truyền thông",
                "Không thuộc ban"
        });
        cbFilterBan.setPreferredSize(new Dimension(135, 28));

        cbFilterKhoa = new JComboBox<>(new String[]{
                "-- Tất cả khóa --",
                "D18",
                "D19",
                "D20"
        });
        cbFilterKhoa.setPreferredSize(new Dimension(115, 28));

        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            if (currentUser.getIdBan() != null && currentUser.getIdBan() >= 1 && currentUser.getIdBan() <= 3) {
                cbFilterBan.setSelectedIndex(currentUser.getIdBan());
            }
            cbFilterBan.setEnabled(false);
        }

        panelSearch.add(new JLabel("Từ khóa:"));
        panelSearch.add(txtTimKiem);
        panelSearch.add(btnTimKiem);
        panelSearch.add(new JLabel("Lọc ban:"));
        panelSearch.add(cbFilterBan);
        panelSearch.add(new JLabel("Lọc khóa:"));
        panelSearch.add(cbFilterKhoa);

        panelCenter.add(panelSearch, BorderLayout.NORTH);

        String[] columnNames = {"Tên Đăng Nhập", "Mã SV", "Họ Tên", "Khóa", "Lớp", "SĐT", "Vai Trò", "Ban", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);
        table.getColumnModel().getColumn(7).setPreferredWidth(110);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        initEvents();
        locDanhSach();
        phanQuyenGiaoDien();
    }

    private void addFormField(JPanel panel, String labelText, Component comp, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        panel.add(comp, gbc);
    }

    private void locDanhSach() {
        String keyword = (txtTimKiem != null) ? txtTimKiem.getText().trim().toLowerCase() : "";
        int indexBan = (cbFilterBan != null) ? cbFilterBan.getSelectedIndex() : 0;
        String selectedKhoa = (cbFilterKhoa != null && cbFilterKhoa.getSelectedIndex() > 0)
                ? cbFilterKhoa.getSelectedItem().toString().trim() : "";

        List<NhanSu> list = nhanSuDAO.getAllNhanSu();
        if (list == null) return;

        tableModel.setRowCount(0);

        for (NhanSu ns : list) {
            if (currentUser != null && currentUser.getIdVaiTro() == 2) {
                int banUser = (currentUser.getIdBan() != null) ? currentUser.getIdBan() : -1;
                int banNS = (ns.getIdBan() != null) ? ns.getIdBan() : -2;
                if (banUser != banNS) {
                    continue;
                }
            } else if (indexBan > 0) {
                int banNS = (ns.getIdBan() != null) ? ns.getIdBan() : 0;
                if (indexBan == 1 && banNS != 1) continue;
                if (indexBan == 2 && banNS != 2) continue;
                if (indexBan == 3 && banNS != 3) continue;
                if (indexBan == 4 && banNS != 0) continue;
            }

            if (!selectedKhoa.isEmpty()) {
                String khoaNS = (ns.getKhoa() != null) ? ns.getKhoa().trim() : "";
                if (!khoaNS.equalsIgnoreCase(selectedKhoa)) {
                    continue;
                }
            }

            if (!keyword.isEmpty()) {
                String tenDN = (ns.getTenDangNhap() != null) ? ns.getTenDangNhap().toLowerCase() : "";
                String maSV = (ns.getMaSV() != null) ? ns.getMaSV().toLowerCase() : "";
                String hoTen = (ns.getHoTen() != null) ? ns.getHoTen().toLowerCase() : "";

                if (!tenDN.contains(keyword) && !maSV.contains(keyword) && !hoTen.contains(keyword)) {
                    continue;
                }
            }

            String tenVaiTro = "Thành viên";
            if (ns.getIdVaiTro() == 1) tenVaiTro = "Quản trị viên";
            else if (ns.getIdVaiTro() == 2) tenVaiTro = "Ban Điều hành";

            String tenBan = "Không thuộc ban";
            if (ns.getIdBan() != null) {
                if (ns.getIdBan() == 1) tenBan = "Ban Đối ngoại";
                else if (ns.getIdBan() == 2) tenBan = "Ban Sự kiện";
                else if (ns.getIdBan() == 3) tenBan = "Ban Truyền thông";
            }

            tableModel.addRow(new Object[]{
                    ns.getTenDangNhap(),
                    ns.getMaSV() != null ? ns.getMaSV() : "",
                    ns.getHoTen() != null ? ns.getHoTen() : "",
                    ns.getKhoa() != null ? ns.getKhoa() : "",
                    ns.getLop() != null ? ns.getLop() : "",
                    ns.getSoDienThoai() != null ? ns.getSoDienThoai() : "",
                    tenVaiTro,
                    tenBan,
                    ns.isTrangThai() ? "Hoạt động" : "Tạm khóa"
            });
        }
    }

    private boolean isAdminUser() {
        if (currentUser == null) return true;
        String roleName = currentUser.getTenVaiTro() != null ? currentUser.getTenVaiTro().trim() : "";
        String userName = currentUser.getTenDangNhap() != null ? currentUser.getTenDangNhap().trim() : "";
        return currentUser.getIdVaiTro() == 1
                || roleName.equalsIgnoreCase("Admin")
                || roleName.equalsIgnoreCase("Quản trị viên")
                || userName.equalsIgnoreCase("admin");
    }

    private void phanQuyenGiaoDien() {
        if (isAdminUser()) {
            btnPhanQuyen.setEnabled(true);
            btnKhoaMoKhoa.setEnabled(true);
            btnResetMK.setEnabled(true);
            cbVaiTro.setEnabled(true);
            cbBan.setEnabled(true);
        } else {
            btnPhanQuyen.setEnabled(false);
            btnKhoaMoKhoa.setEnabled(false);
            btnResetMK.setEnabled(false);

            cbVaiTro.setSelectedItem("Thành viên");
            cbVaiTro.setEnabled(false);

            if (currentUser != null && currentUser.getIdBan() != null) {
                cbBan.setSelectedIndex(currentUser.getIdBan());
                cbBan.setEnabled(false);
            }
        }
    }

    private void initEvents() {
        btnLamMoi.addActionListener(e -> clearForm());

        // Click dòng trên bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtTenDangNhap.setText(table.getValueAt(row, 0).toString());
                    txtTenDangNhap.setEnabled(false);

                    Object maSV = table.getValueAt(row, 1);
                    txtMaSV.setText(maSV != null ? maSV.toString() : "");

                    txtHoTen.setText(table.getValueAt(row, 2).toString());

                    Object khoa = table.getValueAt(row, 3);
                    txtKhoa.setText(khoa != null ? khoa.toString() : "");

                    Object lop = table.getValueAt(row, 4);
                    txtLop.setText(lop != null ? lop.toString() : "");

                    Object sdt = table.getValueAt(row, 5);
                    txtSdt.setText(sdt != null ? sdt.toString() : "");

                    cbVaiTro.setSelectedItem(table.getValueAt(row, 6).toString());
                    cbBan.setSelectedItem(table.getValueAt(row, 7).toString());

                    String trangThai = table.getValueAt(row, 8).toString();
                    chkTrangThai.setSelected(trangThai.equals("Hoạt động"));
                }
            }
        });

        // Thêm mới tài khoản
        btnThem.addActionListener(e -> {
            String tenDN = txtTenDangNhap.getText().trim();
            String maSV = txtMaSV.getText().trim();
            String hoTen = txtHoTen.getText().trim();

            if (tenDN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên đăng nhập!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtTenDangNhap.requestFocus();
                return;
            }
            if (maSV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã SV / Cán bộ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtMaSV.requestFocus();
                return;
            }
            if (hoTen.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Họ và tên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                txtHoTen.requestFocus();
                return;
            }

            NhanSu ns = getFormData();
            if (nhanSuDAO.insertNhanSu(ns)) {
                locDanhSach();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thao tác thêm thất bại!\nNguyên nhân có thể do Tên đăng nhập hoặc Mã SV đã tồn tại trong hệ thống.",
                        "Lỗi thêm dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cập nhật hồ sơ
        btnSua.addActionListener(e -> {
            String tenDN = txtTenDangNhap.getText().trim();
            if (tenDN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự cần sửa trên bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            NhanSu ns = getFormData();
            if (nhanSuDAO.updateNhanSu(ns)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                locDanhSach();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Khóa / Mở khóa tài khoản
        btnKhoaMoKhoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự trên bảng để thay đổi trạng thái!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tenDN = table.getValueAt(row, 0).toString();
            String trangThaiHienTai = table.getValueAt(row, 8).toString();
            boolean trangThaiMoi = !trangThaiHienTai.equals("Hoạt động");

            String action = trangThaiMoi ? "MỞ KHÓA" : "TẠM KHÓA";
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn " + action + " tài khoản: " + tenDN + "?",
                    "Xác nhận thay đổi trạng thái", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (nhanSuDAO.toggleTrangThai(tenDN, trangThaiMoi)) {
                    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái tài khoản!");
                    locDanhSach();
                    chkTrangThai.setSelected(trangThaiMoi);
                } else {
                    JOptionPane.showMessageDialog(this, "Thao tác thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Reset Mật khẩu về 123456
        btnResetMK.addActionListener(e -> {
            String tenDN = txtTenDangNhap.getText().trim();
            if (tenDN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần Reset mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Đặt lại mật khẩu của tài khoản [" + tenDN + "] về mặc định '123456'?",
                    "Xác nhận Reset", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (nhanSuDAO.resetMatKhau(tenDN, "123456")) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu đã được đưa về '123456'!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể reset mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Phân quyền Ban Điều hành (Chỉ giữ 1 listener duy nhất)
        btnPhanQuyen.addActionListener(e -> {
            if (!isAdminUser()) {
                JOptionPane.showMessageDialog(this,
                        "Bạn không có quyền thực hiện chức năng này!\nChỉ Quản trị viên mới có quyền cấp quyền cho Ban Điều hành.",
                        "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân sự từ bảng danh sách!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String vaiTro = table.getValueAt(row, 6).toString();
            if (!vaiTro.equals("Ban Điều hành")) {
                JOptionPane.showMessageDialog(this, "Chức năng này chỉ áp dụng cho tài khoản có vai trò 'Ban Điều hành'!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tenDangNhap = table.getValueAt(row, 0).toString();
            String hoTen = table.getValueAt(row, 2).toString();

            new PhanQuyenDialog(this, tenDangNhap, hoTen).setVisible(true);
        });

        // Tìm kiếm & Lọc
        btnTimKiem.addActionListener(e -> locDanhSach());
        txtTimKiem.addActionListener(e -> locDanhSach());
        cbFilterBan.addActionListener(e -> locDanhSach());
        cbFilterKhoa.addActionListener(e -> locDanhSach());
    }

    private NhanSu getFormData() {
        NhanSu ns = new NhanSu();
        ns.setTenDangNhap(txtTenDangNhap.getText().trim());
        ns.setMaSV(txtMaSV.getText().trim());
        ns.setHoTen(txtHoTen.getText().trim());
        ns.setKhoa(txtKhoa.getText().trim());
        ns.setLop(txtLop.getText().trim());
        ns.setSoDienThoai(txtSdt.getText().trim());
        ns.setTrangThai(chkTrangThai.isSelected());

        if (!isAdminUser() && currentUser != null && currentUser.getIdVaiTro() == 2) {
            ns.setIdVaiTro(3);
            ns.setIdBan(currentUser.getIdBan());
        } else {
            int vtIndex = cbVaiTro.getSelectedIndex();
            if (vtIndex == 0) ns.setIdVaiTro(3);
            else if (vtIndex == 1) ns.setIdVaiTro(2);
            else ns.setIdVaiTro(1);

            int banIndex = cbBan.getSelectedIndex();
            if (banIndex == 0) {
                ns.setIdBan(null);
            } else {
                ns.setIdBan(banIndex);
            }
        }

        return ns;
    }

    private void clearForm() {
        txtTenDangNhap.setText("");
        txtMaSV.setText("");
        txtHoTen.setText("");
        txtKhoa.setText("");
        txtLop.setText("");
        txtSdt.setText("");
        txtTenDangNhap.setEditable(true);
        txtTenDangNhap.setEnabled(true);
        chkTrangThai.setSelected(true);
        table.clearSelection();

        if (!isAdminUser() && currentUser != null && currentUser.getIdVaiTro() == 2) {
            cbVaiTro.setSelectedItem("Thành viên");
            if (currentUser.getIdBan() != null) {
                cbBan.setSelectedIndex(currentUser.getIdBan());
            }
        } else {
            cbVaiTro.setSelectedIndex(0);
            cbBan.setSelectedIndex(0);
        }
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

        menuChucNang.add(menuNhanSu);
        menuChucNang.add(menuCaTruc);
        menuChucNang.add(menuSuKien);
        menuChucNang.add(menuTruyenThong);

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
            new TruyenThongView(currentUser).setVisible(true);
            this.dispose();
        });

        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            Integer idBan = currentUser.getIdBan();
            
            // 1. Mở mặc định theo Ban chuyên môn của Ban Điều hành
            boolean quyenCaTruc = (idBan != null && idBan == 1);     // Ban Đối ngoại / Văn phòng
            boolean quyenSuKien = (idBan != null && idBan == 2);     // Ban Sự kiện
            boolean quyenTruyenThong = (idBan != null && idBan == 3); // Ban Truyền thông

            // 2. Cộng gộp thêm quyền nếu Admin có cấp riêng trong bảng PhanQuyenBanDieuHanh
            com.vanphongdoan.model.PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(currentUser.getTenDangNhap());
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanSuView(null).setVisible(true));
    }
}