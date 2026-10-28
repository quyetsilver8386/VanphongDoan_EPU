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
        setSize(1080, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // CHÈN THANH MENU BAR VÀO CỬA SỔ
        setJMenuBar(createMenuBar());

        // 1. PANEL HEADER (Thanh tiêu đề màu xanh)
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("QUẢN TRỊ TÀI KHOẢN VÀ DANH SÁCH NHÂN SỰ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Xin chào: " + (currentUser != null ? currentUser.getHoTen() : "Admin"));
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // 2. PANEL FORM BÊN TRÁI - "Thông tin tài khoản & nhân sự"
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

        cbVaiTro = new JComboBox<>(new String[]{"Thành viên", "Ban Điều hành", "Admin"});
        cbBan = new JComboBox<>(new String[]{"Không thuộc ban", "Ban Đối ngoại", "Ban Sự kiện", "Ban Truyền thông"});
        chkTrangThai = new JCheckBox("Đang hoạt động", true);

        // Thêm các trường form theo thứ tự rõ ràng
        addFormField(panelForm, "Tên đăng nhập:", txtTenDangNhap, gbc, 0);
        addFormField(panelForm, "Mã SV / Cán bộ:", txtMaSV, gbc, 1);
        addFormField(panelForm, "Họ và tên:", txtHoTen, gbc, 2);
        addFormField(panelForm, "Khóa sinh viên:", txtKhoa, gbc, 3);
        addFormField(panelForm, "Lớp / Chi đoàn:", txtLop, gbc, 4);
        addFormField(panelForm, "Số điện thoại:", txtSdt, gbc, 5);
        addFormField(panelForm, "Vai trò:", cbVaiTro, gbc, 6);
        addFormField(panelForm, "Ban chuyên môn:", cbBan, gbc, 7);

        gbc.gridx = 1; gbc.gridy = 8;
        panelForm.add(chkTrangThai, gbc);

        // Nút chức năng nằm ngay phía dưới
        JPanel panelButtons = new JPanel(new GridLayout(3, 2, 6, 6));
        btnThem = new JButton("Thêm mới");
        btnSua = new JButton("Cập nhật");
        btnKhoaMoKhoa = new JButton("Khóa / Mở");
        btnResetMK = new JButton("Reset MK");
        btnPhanQuyen = new JButton("Phân quyền BĐH"); // Nút mới
        btnLamMoi = new JButton("Làm mới");

        panelButtons.add(btnThem);
        panelButtons.add(btnSua);
        panelButtons.add(btnKhoaMoKhoa);
        panelButtons.add(btnResetMK);
        panelButtons.add(btnPhanQuyen);
        panelButtons.add(btnLamMoi);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        panelForm.add(panelButtons, gbc);

        add(panelForm, BorderLayout.WEST);

        // 3. PANEL CENTER BÊN PHẢI - "Danh sách nhân sự trong hệ thống"
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Danh sách nhân sự trong hệ thống"));

        // THANH TÌM KIẾM VÀ BỘ LỌC ĐA NĂNG
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        txtTimKiem = new JTextField(12);
        txtTimKiem.setPreferredSize(new Dimension(140, 28));

        btnTimKiem = new JButton("Tìm");
        btnTimKiem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Khởi tạo các ComboBox bộ lọc
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

        // Nếu là Ban Điều hành (vai trò 2), khóa cứng ban của họ
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
        table.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(130); // Tên Đăng Nhập
        table.getColumnModel().getColumn(1).setPreferredWidth(90);  // Mã SV
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Họ Tên
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // SĐT
        table.getColumnModel().getColumn(6).setPreferredWidth(110); // Vai Trò
        table.getColumnModel().getColumn(7).setPreferredWidth(110); // Ban
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // KHỞI TẠO SỰ KIỆN VÀ NẠP DỮ LIỆU TỪ SQL SERVER
        initEvents();
        // Gọi lọc ngay khi mở form để bảng nạp danh sách thành viên
        locDanhSach();
        // KIỂM TRA PHÂN QUYỀN GIAO DIỆN THEO USER ĐANG ĐĂNG NHẬP
        phanQuyenGiaoDien();
    }

    private void addFormField(JPanel panel, String labelText, Component comp, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(comp, gbc);
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<NhanSu> list;

        // Nếu là Ban Điều hành (ID_VaiTro == 2) và có ID_Ban -> chỉ lấy nhân sự của ban đó
        if (currentUser != null && currentUser.getIdVaiTro() == 2 && currentUser.getIdBan() != null) {
            list = nhanSuDAO.getNhanSuByBan(currentUser.getIdBan());
        } else {
            // Admin hoặc người quản trị chung -> lấy toàn bộ danh sách
            list = nhanSuDAO.getAllNhanSu();
        }

        for (NhanSu ns : list) {
            tableModel.addRow(new Object[]{
                ns.getTenDangNhap(),
                ns.getMaSV(),
                ns.getHoTen(),
                ns.getKhoa(),
                ns.getLop(),
                ns.getSoDienThoai(),
                ns.getTenVaiTro(),
                ns.getTenBan(),
                ns.isTrangThai() ? "Hoạt động" : "Tạm khóa"
            });
        }
    }
    private void locDanhSach() {
        String keyword = (txtTimKiem != null) ? txtTimKiem.getText().trim().toLowerCase() : "";
        int indexBan = (cbFilterBan != null) ? cbFilterBan.getSelectedIndex() : 0;
        String selectedKhoa = (cbFilterKhoa != null && cbFilterKhoa.getSelectedIndex() > 0) 
                ? cbFilterKhoa.getSelectedItem().toString().trim() : "";

        // 1. Lấy toàn bộ dữ liệu từ DAO (hàm đã chạy ổn định sẵn có)
        List<NhanSu> list = nhanSuDAO.getAllNhanSu();
        if (list == null) return;

        // 2. Xóa các hàng cũ trên bảng
        tableModel.setRowCount(0);

        // 3. Quét và lọc danh sách
        for (NhanSu ns : list) {
            // A. Kiểm tra Phân quyền Ban (Ưu tiên số 1):
            // Nếu là BĐH: Chỉ giữ lại người cùng ID_Ban với mình
            if (currentUser != null && currentUser.getIdVaiTro() == 2) {
                int banUser = (currentUser.getIdBan() != null) ? currentUser.getIdBan() : -1;
                int banNS = (ns.getIdBan() != null) ? ns.getIdBan() : -2;
                if (banUser != banNS) {
                    continue; // Bỏ qua nếu không cùng Ban
                }
            } 
            // Nếu là Admin: Lọc theo vị trí chọn trên ComboBox
            else if (indexBan > 0) {
                int banNS = (ns.getIdBan() != null) ? ns.getIdBan() : 0;
                if (indexBan == 1 && banNS != 1) continue; // Ban Đối ngoại (ID = 1)
                if (indexBan == 2 && banNS != 2) continue; // Ban Sự kiện (ID = 2)
                if (indexBan == 3 && banNS != 3) continue; // Ban Truyền thông (ID = 3)
                if (indexBan == 4 && banNS != 0) continue; // Không thuộc ban (ID = 0 hoặc null)
            }

            // B. Kiểm tra Lọc theo Khóa (D18, D19, D20):
            if (!selectedKhoa.isEmpty()) {
                String khoaNS = (ns.getKhoa() != null) ? ns.getKhoa().trim() : "";
                if (!khoaNS.equalsIgnoreCase(selectedKhoa)) {
                    continue;
                }
            }

            // C. Kiểm tra Từ khóa tìm kiếm (Tên đăng nhập, Mã SV, Họ tên):
            if (!keyword.isEmpty()) {
                String tenDN = (ns.getTenDangNhap() != null) ? ns.getTenDangNhap().toLowerCase() : "";
                String maSV = (ns.getMaSV() != null) ? ns.getMaSV().toLowerCase() : "";
                String hoTen = (ns.getHoTen() != null) ? ns.getHoTen().toLowerCase() : "";

                if (!tenDN.contains(keyword) && !maSV.contains(keyword) && !hoTen.contains(keyword)) {
                    continue;
                }
            }

            // D. Chuyển đổi ID sang tên hiển thị Tiếng Việt chuẩn
            String tenVaiTro = "Thành viên";
            if (ns.getIdVaiTro() == 1) tenVaiTro = "Quản trị viên";
            else if (ns.getIdVaiTro() == 2) tenVaiTro = "Ban Điều hành";

            String tenBan = "Không thuộc ban";
            if (ns.getIdBan() != null) {
                if (ns.getIdBan() == 1) tenBan = "Ban Đối ngoại";
                else if (ns.getIdBan() == 2) tenBan = "Ban Sự kiện";
                else if (ns.getIdBan() == 3) tenBan = "Ban Truyền thông";
            }

            // E. Đưa dữ liệu hợp lệ lên JTable
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
    private void phanQuyenGiaoDien() {
        if (currentUser == null || currentUser.getIdVaiTro() != 1) {
            // 1. Khóa các chức năng quản trị cấp cao của Admin
            btnPhanQuyen.setEnabled(false);
            btnKhoaMoKhoa.setEnabled(false);
            btnResetMK.setEnabled(false);

            // 2. Mặc định tài khoản thêm mới bởi BĐH phải là 'Thành viên'
            cbVaiTro.setSelectedItem("Thành viên");
            cbVaiTro.setEnabled(false);

            // 3. Khóa cứng Ban chuyên môn theo Ban của chính người đang đăng nhập
            if (currentUser != null && currentUser.getIdBan() != null) {
                // Tự động chọn đúng ban theo ID của BĐH
                cbBan.setSelectedIndex(currentUser.getIdBan()); 
                cbBan.setEnabled(false); // Không cho BĐH chọn sang ban khác
            }
        }
    }

    private void initEvents() {
        btnLamMoi.addActionListener(e -> {
            clearForm();
        });

        // Mouse Listener khi click dòng trên JTable
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtTenDangNhap.setText(table.getValueAt(row, 0).toString());
                    txtTenDangNhap.setEnabled(false); // Đã chọn tài khoản có sẵn thì không sửa Username

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

        // Nút Thêm mới tài khoản
        btnThem.addActionListener(e -> {
            String tenDN = txtTenDangNhap.getText().trim();
            String maSV = txtMaSV.getText().trim();
            String hoTen = txtHoTen.getText().trim();

            // 1. Kiểm tra các trường bắt buộc
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

            // 2. Lấy dữ liệu và thực thi lưu
            NhanSu ns = getFormData();
            if (nhanSuDAO.insertNhanSu(ns)) {
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!\nMật khẩu khởi tạo mặc định: 123456");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Thao tác thêm thất bại!\nNguyên nhân có thể do Tên đăng nhập hoặc Mã SV đã tồn tại trong hệ thống.", 
                        "Lỗi thêm dữ liệu", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Sửa hồ sơ & quyền hạn
        btnSua.addActionListener(e -> {
            String tenDN = txtTenDangNhap.getText().trim();
            if (tenDN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự cần sửa trên bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            NhanSu ns = getFormData();
            if (nhanSuDAO.updateNhanSu(ns)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                loadDataToTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Khóa / Mở khóa tài khoản (thay cho Xóa cứng)
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
                    loadDataToTable();
                    chkTrangThai.setSelected(trangThaiMoi);
                } else {
                    JOptionPane.showMessageDialog(this, "Thao tác thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Nút Reset Mật khẩu về 123456
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

        // Nút Tìm kiếm
        btnTimKiem.addActionListener(e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            tableModel.setRowCount(0);
            
            List<NhanSu> list;
            if (currentUser != null && currentUser.getIdVaiTro() == 2 && currentUser.getIdBan() != null) {
                list = nhanSuDAO.getNhanSuByBan(currentUser.getIdBan());
            } else {
                list = nhanSuDAO.getAllNhanSu();
            }

            for (NhanSu ns : list) {
                boolean match = ns.getTenDangNhap().toLowerCase().contains(keyword) ||
                                (ns.getMaSV() != null && ns.getMaSV().toLowerCase().contains(keyword)) ||
                                (ns.getHoTen() != null && ns.getHoTen().toLowerCase().contains(keyword));
                if (match) {
                    tableModel.addRow(new Object[]{
                        ns.getTenDangNhap(),
                        ns.getMaSV(),
                        ns.getHoTen(),
                        ns.getKhoa(),
                        ns.getLop(),
                        ns.getSoDienThoai(),
                        ns.getTenVaiTro(),
                        ns.getTenBan(),
                        ns.isTrangThai() ? "Hoạt động" : "Tạm khóa"
                    });
                }
            }
        });
        btnPhanQuyen.addActionListener(e -> {
            // Chặn trực tiếp nếu không phải Admin
            if (currentUser == null || currentUser.getIdVaiTro() != 1) {
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
        // Sự kiện khi bấm nút Tìm
        btnTimKiem.addActionListener(e -> locDanhSach());

        // Sự kiện khi nhấn Enter trong ô tìm kiếm
        txtTimKiem.addActionListener(e -> locDanhSach());

        // Sự kiện lọc tức thì khi chọn Ban khác
        cbFilterBan.addActionListener(e -> locDanhSach());

        // Sự kiện lọc tức thì khi chọn Khóa khác
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

        // Kiểm tra phân quyền khi gán dữ liệu:
        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            // NẾU LÀ BAN ĐIỀU HÀNH THÊM MỚI:
            // 1. Luôn là Thành viên (ID = 3)
            ns.setIdVaiTro(3); 
            // 2. Luôn gắn chặt vào đúng Ban của BĐH đó
            ns.setIdBan(currentUser.getIdBan()); 
        } else {
            // NẾU LÀ ADMIN (Toàn quyền chọn):
            int vtIndex = cbVaiTro.getSelectedIndex();
            if (vtIndex == 0) ns.setIdVaiTro(3);      // Thành viên
            else if (vtIndex == 1) ns.setIdVaiTro(2); // Ban Điều hành
            else ns.setIdVaiTro(1);                  // Admin

            int banIndex = cbBan.getSelectedIndex();
            if (banIndex == 0) {
                ns.setIdBan(null);
            } else {
                ns.setIdBan(banIndex);
            }
        }

        return ns;
    }
    
    private void kiemTraQuyenMenu() {
        // 1. Nếu là Admin (ID_VaiTro == 1): Toàn quyền, tất cả các menu đều mở
        if (currentUser != null && currentUser.getIdVaiTro() == 1) {
            menuNhanSu.setEnabled(true);
            menuCaTruc.setEnabled(true);
            menuSuKien.setEnabled(true);
            menuTruyenThong.setEnabled(true);
            return;
        }

        // 2. Nếu là Ban Điều hành (ID_VaiTro == 2): Đọc quyền chi tiết từ DB
        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            // Quản lý nhân sự: BĐH luôn được vào để xem/quản lý thành viên trong ban của mình
            menuNhanSu.setEnabled(true);

            // Lấy quyền chi tiết của user hiện tại
            com.vanphongdoan.model.PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(currentUser.getTenDangNhap());

            if (pq != null) {
                // Quyền Đối ngoại -> Quản lý Ca trực
                menuCaTruc.setEnabled(pq.isQuyenDoiNgoai());
                if (!pq.isQuyenDoiNgoai()) {
                    menuCaTruc.setToolTipText("Bạn không được phân quyền quản lý Ca trực!");
                }

                // Quyền Sự kiện -> Quản lý Sự kiện
                menuSuKien.setEnabled(pq.isQuyenSuKien());
                if (!pq.isQuyenSuKien()) {
                    menuSuKien.setToolTipText("Bạn không được phân quyền quản lý Sự kiện!");
                }

                // Quyền Truyền thông -> Quản lý Truyền thông
                menuTruyenThong.setEnabled(pq.isQuyenTruyenThong());
                if (!pq.isQuyenTruyenThong()) {
                    menuTruyenThong.setToolTipText("Bạn không được phân quyền quản lý Truyền thông!");
                }
            } else {
                // Nếu chưa được tích quyền nào trong DB thì khóa cả 3 nghiệp vụ
                menuCaTruc.setEnabled(false);
                menuSuKien.setEnabled(false);
                menuTruyenThong.setEnabled(false);
            }
        }
    }

    // Khai báo các JMenuItem trong class
    private JMenuItem menuNhanSu;
    private JMenuItem menuCaTruc;
    private JMenuItem menuSuKien;
    private JMenuItem menuTruyenThong; // Thêm mới mục Quản lý Truyền thông

    // Trong hàm tạo Menu:
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Hệ thống
        JMenu menuHeThong = new JMenu("Hệ thống");
        JMenuItem menuDangXuat = new JMenuItem("Đăng xuất");
        JMenuItem menuThoat = new JMenuItem("Thoát");
        menuHeThong.add(menuDangXuat);
        menuHeThong.addSeparator();
        menuHeThong.add(menuThoat);

        // Menu Quản lý Chức năng
        JMenu menuChucNang = new JMenu("Quản lý Chức năng");
        menuNhanSu = new JMenuItem("Quản lý Nhân sự");
        menuCaTruc = new JMenuItem("Quản lý Ca trực");
        menuSuKien = new JMenuItem("Quản lý Sự kiện");
        menuTruyenThong = new JMenuItem("Quản lý Truyền thông"); // Thêm mục này vào Menu

        menuChucNang.add(menuNhanSu);
        menuChucNang.add(menuCaTruc);
        menuChucNang.add(menuSuKien);
        menuChucNang.add(menuTruyenThong);

        menuBar.add(menuHeThong);
        menuBar.add(menuChucNang);
        setJMenuBar(menuBar);

        // Xử lý sự kiện click chuyển màn hình
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
            // Mở màn hình Truyền thông (NhiemVuTruyenThongView)
            JOptionPane.showMessageDialog(this, "Đang mở Quản lý Truyền thông...");
        });

        menuDangXuat.addActionListener(e -> {
            new LoginView().setVisible(true);
            this.dispose();
        });
        menuThoat.addActionListener(e -> System.exit(0));

        // ÁP DỤNG PHÂN QUYỀN TRÊN MENU BAR
        kiemTraQuyenMenu();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanSuView(null).setVisible(true));
    }
    private void clearForm() {
        txtTenDangNhap.setText("");
        txtMaSV.setText("");
        txtHoTen.setText("");
        txtKhoa.setText("");
        txtLop.setText("");
        txtSdt.setText("");
        txtTenDangNhap.setEnabled(true);
        chkTrangThai.setSelected(true);

        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
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

        // 1. Menu Hệ thống
        JMenu menuHeThong = new JMenu("Hệ thống");
        JMenuItem menuDangXuat = new JMenuItem("Đăng xuất");
        JMenuItem menuThoat = new JMenuItem("Thoát");

        menuDangXuat.addActionListener(e -> {
            new com.vanphongdoan.view.LoginView().setVisible(true);
            this.dispose();
        });
        menuThoat.addActionListener(e -> System.exit(0));

        menuHeThong.add(menuDangXuat);
        menuHeThong.addSeparator();
        menuHeThong.add(menuThoat);

        // 2. Menu Quản lý Chức năng
        JMenu menuChucNang = new JMenu("Quản lý Chức năng");
        JMenuItem menuNhanSu = new JMenuItem("Quản lý Nhân sự");
        JMenuItem menuCaTruc = new JMenuItem("Quản lý Ca trực");
        JMenuItem menuSuKien = new JMenuItem("Quản lý Sự kiện");
        JMenuItem menuTruyenThong = new JMenuItem("Quản lý Truyền thông");

        menuChucNang.add(menuNhanSu);
        menuChucNang.add(menuCaTruc);
        menuChucNang.add(menuSuKien);
        menuChucNang.add(menuTruyenThong);

        // 1. Quản lý Nhân sự
        menuNhanSu.addActionListener(e -> {
            new NhanSuView(currentUser).setVisible(true);
            this.dispose();
        });

        // 2. Quản lý Ca trực (Mở đúng CaTrucView)
        menuCaTruc.addActionListener(e -> {
            new CaTrucView(currentUser).setVisible(true);
            this.dispose();
        });

        // 3. Quản lý Sự kiện (Mở đúng SuKienView)
        menuSuKien.addActionListener(e -> {
            new SuKienView(currentUser).setVisible(true); // <-- Sửa dòng này thành SuKienView
            this.dispose();
        });

        // 4. Quản lý Truyền thông (Mở đúng TruyenThongView)
        menuTruyenThong.addActionListener(e -> {
            new TruyenThongView(currentUser).setVisible(true);
            this.dispose();
        });

        // 3. Phân quyền menu bar theo vai trò tài khoản
        if (currentUser != null && currentUser.getIdVaiTro() == 2) {
            // Nếu là Ban Điều hành -> kiểm tra bảng PhanQuyenBanDieuHanh
            com.vanphongdoan.model.PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(currentUser.getTenDangNhap());
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
}