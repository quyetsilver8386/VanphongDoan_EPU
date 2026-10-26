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
    private JTextField txtMaSV, txtHoTen, txtLop, txtSdt, txtTimKiem;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cbVaiTro, cbBan;
    private JCheckBox chkTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    
    // Khai báo DAO để thao tác dữ liệu
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

        setTitle("QUAN LY NHAN SU - HE THONG VAN PHONG DOAN");
        setSize(1000, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 0. CHÈN THANH MENU BAR VÀO CỬA SỔ
        setJMenuBar(createMenuBar());

        // 1. PANEL HEADER (Thanh tiêu đề trên cùng)
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("QUAN LY DANH SACH NHAN SU");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Xin chao: " + (currentUser != null ? currentUser.getHoTen() : "Admin"));
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // 2. PANEL FORM (Khung nhập liệu bên trái)
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Thong tin nhan su"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaSV = new JTextField(15);
        txtMatKhau = new JPasswordField(15);
        txtHoTen = new JTextField(15);
        txtLop = new JTextField(15);
        txtSdt = new JTextField(15);
        
        cbVaiTro = new JComboBox<>(new String[]{"Admin", "Ban Dieu Hanh", "Thanh vien"});
        cbBan = new JComboBox<>(new String[]{"Khong thuoc ban", "Ban Doi Ngoai", "Ban Su Kien", "Ban Truyen Thong"});
        chkTrangThai = new JCheckBox("Dang hoat dong", true);

        addFormField(panelForm, "Ma SV:", txtMaSV, gbc, 0);
        addFormField(panelForm, "Mat khau:", txtMatKhau, gbc, 1);
        addFormField(panelForm, "Ho ten:", txtHoTen, gbc, 2);
        addFormField(panelForm, "Lop:", txtLop, gbc, 3);
        addFormField(panelForm, "So dien thoai:", txtSdt, gbc, 4);
        addFormField(panelForm, "Vai tro:", cbVaiTro, gbc, 5);
        addFormField(panelForm, "Ban:", cbBan, gbc, 6);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panelForm.add(chkTrangThai, gbc);

        // Nút chức năng
        JPanel panelButtons = new JPanel(new GridLayout(2, 2, 5, 5));
        btnThem = new JButton("Them");
        btnSua = new JButton("Sua");
        btnXoa = new JButton("Xoa");
        btnLamMoi = new JButton("Lam moi");
        panelButtons.add(btnThem); panelButtons.add(btnSua);
        panelButtons.add(btnXoa); panelButtons.add(btnLamMoi);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        panelForm.add(panelButtons, gbc);
        add(panelForm, BorderLayout.WEST);

        // 3. PANEL CENTER (Bảng dữ liệu bên phải)
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Danh sach nhan su trong he thong"));
        
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tim kiem");
        panelSearch.add(new JLabel("Tim kiem (MaSV/HoTen):"));
        panelSearch.add(txtTimKiem); panelSearch.add(btnTimKiem);
        panelCenter.add(panelSearch, BorderLayout.NORTH);

        String[] columnNames = {"Ma SV", "Ho ten", "Lop", "SDT", "Vai Tro (ID)", "Ban (ID)", "Trang thai"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);


        initEvents();
        loadDataToTable();
    }

    private void addFormField(JPanel panel, String labelText, Component comp, GridBagConstraints gbc, int row) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(comp, gbc);
    }

    private void initEvents() {
        btnLamMoi.addActionListener(e -> clearForm());

        // Bấm chuột vào bảng để tự đổ ngược thông tin lên Form bên trái
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaSV.setText(table.getValueAt(row, 0).toString());
                    txtHoTen.setText(table.getValueAt(row, 1).toString());
                    txtLop.setText(table.getValueAt(row, 2).toString());
                    txtSdt.setText(table.getValueAt(row, 3).toString());
                    
                    int idVaiTro = Integer.parseInt(table.getValueAt(row, 4).toString());
                    cbVaiTro.setSelectedIndex(idVaiTro - 1); 
                    
                    String trangThai = table.getValueAt(row, 6).toString();
                    chkTrangThai.setSelected(trangThai.equals("Hoat dong"));
                    
                    txtMaSV.setEnabled(false); // Đã chọn trên bảng thì khóa không cho sửa Mã SV
                }
            }
        });

        btnThem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Them...");
        });

        btnSua.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Sua...");
        });

        btnXoa.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Ban co chac muon xoa Nhan su nay?", "Xac nhan", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Xoa...");
            }
        });
    }

    private void clearForm() {
        txtMaSV.setText(""); txtMaSV.setEnabled(true);
        txtMatKhau.setText(""); txtHoTen.setText("");
        txtLop.setText(""); txtSdt.setText("");
        cbVaiTro.setSelectedIndex(0); cbBan.setSelectedIndex(0);
        chkTrangThai.setSelected(true);
    }

    // HÀM TẠO THANH THỰC ĐƠN (MENU BAR)
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu 1: Hệ thống
        JMenu menuHeThong = new JMenu("Hệ thống");
        JMenuItem miDangXuat = new JMenuItem("Đăng xuất");
        JMenuItem miThoat = new JMenuItem("Thoát");

        miDangXuat.addActionListener(e -> {
            this.dispose();
            new LoginView().setVisible(true);
        });

        miThoat.addActionListener(e -> System.exit(0));

        menuHeThong.add(miDangXuat);
        menuHeThong.addSeparator();
        menuHeThong.add(miThoat);

        // Menu 2: Quản lý Chức năng
        JMenu menuQuanLy = new JMenu("Quản lý Chức năng");
        JMenuItem miNhanSu = new JMenuItem("Quản lý Nhân sự");
        JMenuItem miCaTruc = new JMenuItem("Quản lý Ca trực");
        JMenuItem miSuKien = new JMenuItem("Quản lý Sự kiện");
        JMenuItem miTruyenThong = new JMenuItem("Nhiệm vụ Truyền thông");

        miNhanSu.addActionListener(e -> {
            // Đang ở màn hình Nhân sự
        });

        miCaTruc.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chuyển sang Màn hình Quản lý Ca trực!");
        });

        miSuKien.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chuyển sang Màn hình Quản lý Sự kiện!");
        });

        menuQuanLy.add(miNhanSu);
        menuQuanLy.add(miCaTruc);
        menuQuanLy.add(miSuKien);
        menuQuanLy.add(miTruyenThong);

        menuBar.add(menuHeThong);
        menuBar.add(menuQuanLy);

        return menuBar;
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        List<NhanSu> list = nhanSuDAO.getAllNhanSu();
        for (NhanSu ns : list) {
            tableModel.addRow(new Object[]{
                ns.getMaSV(), 
                ns.getHoTen(), 
                ns.getLop(), 
                ns.getSoDienThoai(), 
                ns.getIdVaiTro(), 
                ns.getIdBan() == null ? "0" : ns.getIdBan(), 
                ns.isTrangThai() ? "Hoat dong" : "Khoa"
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NhanSuView(null).setVisible(true));
    }
}