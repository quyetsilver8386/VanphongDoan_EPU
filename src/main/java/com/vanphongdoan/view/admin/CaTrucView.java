package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.CaTrucDAO;
import com.vanphongdoan.model.CaTruc;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CaTrucView extends JFrame {

    private NhanSu currentUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtIdCa, txtNgayTruc, txtMaSV, txtTimKiem, txtGhiChu;
    private JComboBox<String> cbTenCa, cbTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    private CaTrucDAO caTrucDAO = new CaTrucDAO();

    public CaTrucView(NhanSu ns) {
        this.currentUser = ns;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("QUAN LY CA TRUC - HE THONG VAN PHONG DOAN");
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

        JLabel lblTitle = new JLabel("QUAN LY LICH TRUC VAN PHONG DOAN");
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
        panelForm.setBorder(BorderFactory.createTitledBorder("Thong tin ca truc"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIdCa = new JTextField(15);
        txtIdCa.setEnabled(false); // Khóa vì ID ca trực tự tăng
        cbTenCa = new JComboBox<>(new String[]{"Ca Sang (08:00 - 11:30)", "Ca Chieu (13:30 - 17:00)", "Ca Toi (18:00 - 20:30)"});
        txtNgayTruc = new JTextField(15);
        txtMaSV = new JTextField(15);
        cbTrangThai = new JComboBox<>(new String[]{"Chua truc", "Da truc", "Vang"});
        txtGhiChu = new JTextField(15);

        addFormField(panelForm, "ID Ca:", txtIdCa, gbc, 0);
        addFormField(panelForm, "Ten ca:", cbTenCa, gbc, 1);
        addFormField(panelForm, "Ngay truc (YYYY-MM-DD):", txtNgayTruc, gbc, 2);
        addFormField(panelForm, "Ma SV truc:", txtMaSV, gbc, 3);
        addFormField(panelForm, "Trang thai:", cbTrangThai, gbc, 4);
        addFormField(panelForm, "Ghi chu:", txtGhiChu, gbc, 5);

        // Nút chức năng
        JPanel panelButtons = new JPanel(new GridLayout(2, 2, 5, 5));
        btnThem = new JButton("Them ca truc");
        btnSua = new JButton("Sua ca truc");
        btnXoa = new JButton("Xoa ca truc");
        btnLamMoi = new JButton("Lam moi");
        panelButtons.add(btnThem); panelButtons.add(btnSua);
        panelButtons.add(btnXoa); panelButtons.add(btnLamMoi);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panelForm.add(panelButtons, gbc);
        add(panelForm, BorderLayout.WEST);

        // 3. PANEL CENTER (Bảng dữ liệu bên phải)
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Danh sach ca truc van phong"));

        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTimKiem = new JTextField(20);
        btnTimKiem = new JButton("Tim kiem");
        panelSearch.add(new JLabel("Tim kiem (MaSV/Ngay):"));
        panelSearch.add(txtTimKiem); panelSearch.add(btnTimKiem);
        panelCenter.add(panelSearch, BorderLayout.NORTH);

        String[] columnNames = {"ID Ca", "Ten ca", "Ngay truc", "Ma SV", "Trang thai", "Ghi chu"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panelCenter.add(scrollPane, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // KHỞI TẠO CÁC SỰ KIỆN NÚT BẤM VÀ MOUSE CLICK
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

        // Bấm chuột vào bảng để đổ ngược thông tin lên Form bên trái
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtIdCa.setText(table.getValueAt(row, 0).toString());
                    
                    String tenCa = table.getValueAt(row, 1).toString();
                    cbTenCa.setSelectedItem(tenCa);
                    
                    txtNgayTruc.setText(table.getValueAt(row, 2).toString());
                    txtMaSV.setText(table.getValueAt(row, 3) != null ? table.getValueAt(row, 3).toString() : "");
                    
                    String trangThai = table.getValueAt(row, 4).toString();
                    cbTrangThai.setSelectedItem(trangThai);
                    
                    txtGhiChu.setText(table.getValueAt(row, 5) != null ? table.getValueAt(row, 5).toString() : "");
                }
            }
        });

        btnThem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Them ca truc...");
        });

        btnSua.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Sua ca truc...");
        });

        btnXoa.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Ban co chac muon xoa Ca truc nay?", "Xac nhan", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Xoa ca truc...");
            }
        });

        btnTimKiem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Dang xu ly chuc nang Tim kiem ca truc...");
        });
    }

    private void clearForm() {
        txtIdCa.setText("");
        cbTenCa.setSelectedIndex(0);
        txtNgayTruc.setText("");
        txtMaSV.setText("");
        cbTrangThai.setSelectedIndex(0);
        txtGhiChu.setText("");
    }

    // HÀM TẠO THANH THỰC ĐƠN (MENU BAR) ĐIỀU HƯỚNG GIỮA CÁC MÀN HÌNH
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
            // Đang ở CaTrucView
        });

        miSuKien.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Chuyển sang Màn hình Quản lý Sự kiện!");
        });

        menuQuanLy.add(miNhanSu);
        menuQuanLy.add(miCaTruc);
        menuQuanLy.add(miSuKien);

        menuBar.add(menuHeThong);
        menuBar.add(menuQuanLy);

        return menuBar;
    }
    private void loadDataToTable() {
    tableModel.setRowCount(0);
    List<CaTruc> list = caTrucDAO.getAllCaTruc();
    for (CaTruc ct : list) {
        tableModel.addRow(new Object[]{
            ct.getIdCa(),
            ct.getTenCa(),
            ct.getNgayTruc() != null ? ct.getNgayTruc().toString() : "",
            ct.getMaSV() != null ? ct.getMaSV() : "Chưa phân công",
            ct.getTrangThai(),
            ct.getGhiChu() != null ? ct.getGhiChu() : ""
        });
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CaTrucView(null).setVisible(true));
    }
}