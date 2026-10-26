package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.SuKienDAO;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SuKienView extends JFrame {

    private NhanSu currentUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtIdSuKien, txtTenSuKien, txtNgayToChuc, txtDiaDiem, txtKinhPhi, txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private SuKienDAO suKienDAO = new SuKienDAO();

    // HÀM KHỞI TẠO CÓ THAM SỐ NHẬN THÔNG TIN NGƯỜI DÙNG ĐĂNG NHẬP
    public SuKienView(NhanSu ns) {
        this.currentUser = ns;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("QUAN LY SU KIEN - HE THONG VAN PHONG DOAN");
        setSize(1000, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // CHÈN THANH MENU BAR VÀO CỬA SỔ
        setJMenuBar(createMenuBar());

        // 1. PANEL HEADER (Thanh tiêu đề trên cùng)
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("QUAN LY SU KIEN DOAN TRUONG");
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
        panelForm.setBorder(BorderFactory.createTitledBorder("Thong tin su kien"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIdSuKien = new JTextField(15);
        txtIdSuKien.setEnabled(false); // Khóa không cho sửa ID tự tăng
        txtTenSuKien = new JTextField(15);
        txtNgayToChuc = new JTextField(15);
        txtDiaDiem = new JTextField(15);
        txtKinhPhi = new JTextField(15);
        cbTrangThai = new JComboBox<>(new String[]{"Ke hoach", "Dang dien ra", "Da hoan thanh", "Huy"});

        addFormField(panelForm, "ID Su kien:", txtIdSuKien, gbc, 0);
        addFormField(panelForm, "Ten su kien:", txtTenSuKien, gbc, 1);
        addFormField(panelForm, "Ngay to chuc (YYYY-MM-DD):", txtNgayToChuc, gbc, 2);
        addFormField(panelForm, "Dia diem:", txtDiaDiem, gbc, 3);
        addFormField(panelForm, "Kinh phi:", txtKinhPhi, gbc, 4);
        addFormField(panelForm, "Trang thai:", cbTrangThai, gbc, 5);

        // Nút chức năng
        JPanel panelButtons = new JPanel(new GridLayout(2, 2, 5, 5));
        btnThem = new JButton("Them su kien");
        btnSua = new JButton("Sua su kien");
        btnXoa = new JButton("Xoa su kien");
        btnLamMoi = new JButton("Lam moi");
        panelButtons.add(btnThem); panelButtons.add(btnSua);
        panelButtons.add(btnXoa); panelButtons.add(btnLamMoi);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panelForm.add(panelButtons, gbc);
        add(panelForm, BorderLayout.WEST);

        // 3. PANEL CENTER (Bảng dữ liệu bên phải)
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Danh sach su kien Doan thanh nien"));

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tim kiem");
        panelSearch.add(new JLabel("Tim kiem (Ten/Dia diem):"));
        panelSearch.add(txtTimKiem); panelSearch.add(btnTimKiem);
        panelCenter.add(panelSearch, BorderLayout.NORTH);

        String[] columnNames = {"ID Su Kien", "Ten su kien", "Ngay to chuc", "Dia diem", "Kinh phi", "Trang thai"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        initEvents();
    }

    // HÀM KHỞI TẠO KHÔNG THAM SỐ (MẶC ĐỊNH CALL CONSTRUCTOR TRÊN VỚI NULL)
    public SuKienView() {
        this(null);
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

        // Bấm chuột vào dòng dữ liệu trên JTable
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtIdSuKien.setText(table.getValueAt(row, 0).toString());
                    txtTenSuKien.setText(table.getValueAt(row, 1).toString());
                    txtNgayToChuc.setText(table.getValueAt(row, 2) != null ? table.getValueAt(row, 2).toString() : "");
                    txtDiaDiem.setText(table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "");
                    txtKinhPhi.setText(table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "");
                    cbTrangThai.setSelectedItem(table.getValueAt(row, 5).toString());
                }
            }
        });

        btnThem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Them su kien..."));
        btnSua.addActionListener(e -> JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Sua su kien..."));
        btnXoa.addActionListener(e -> JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Xoa su kien..."));
        btnTimKiem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Tim kiem su kien..."));
    }

    private void clearForm() {
        txtIdSuKien.setText("");
        txtTenSuKien.setText("");
        txtNgayToChuc.setText("");
        txtDiaDiem.setText("");
        txtKinhPhi.setText("");
        cbTrangThai.setSelectedIndex(0);
    }

    // TẠO MENUBAR ĐIỀU HƯỚNG SANG CÁC MÀN HÌNH KHÁC
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

        miNhanSu.addActionListener(e -> {
            this.dispose();
            new NhanSuView(currentUser).setVisible(true);
        });

        miCaTruc.addActionListener(e -> {
            this.dispose();
            new CaTrucView(currentUser).setVisible(true);
        });

        miSuKien.addActionListener(e -> {
            // Đang ở SuKienView
        });

        menuQuanLy.add(miNhanSu);
        menuQuanLy.add(miCaTruc);
        menuQuanLy.add(miSuKien);

        menuBar.add(menuHeThong);
        menuBar.add(menuQuanLy);

        return menuBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuKienView(null).setVisible(true));
    }
}