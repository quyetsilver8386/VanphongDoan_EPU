package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.CaTrucDAO;
import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.model.CaTruc;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.view.LoginView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaTrucView extends JFrame {

    private NhanSu currentUser;
    private CaTrucDAO caTrucDAO = new CaTrucDAO();
    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    // Quản lý tuần
    private LocalDate mondayOfWeek;
    private JLabel lblTuanHienTai;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Ma trận thời khóa biểu
    private JTable timetable;
    private DefaultTableModel timetableModel;
    private Map<String, CaTruc> caTrucMap = new HashMap<>();

    // Form tác nghiệp bên trái
    private JLabel lblNgayChon;
    private JLabel lblCaChon;
    private JLabel lblTrangThaiHienTai;
    private JComboBox<String> cbChonNhanSu;
    private JButton btnPhanCong;
    private JButton btnHuyPhanCong;
    private JButton btnDongMoCa;
    private JButton btnTaoLichTuan;

    private CaTruc selectedCaTruc = null;
    private int selectedRow = -1;
    private int selectedCol = -1;

    // Menu Bar
    private JMenuItem menuNhanSu;
    private JMenuItem menuCaTruc;
    private JMenuItem menuSuKien;
    private JMenuItem menuTruyenThong;

    public CaTrucView() {
        this(null);
    }

    public CaTrucView(NhanSu user) {
        this.currentUser = user;
        this.mondayOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);

        initComponents();
        loadNhanSuToComboBox();
        loadDataToTimetable();
    }

    private void initComponents() {
        setTitle("QUẢN LÝ LỊCH TRỰC VĂN PHÒNG ĐOÀN - EPU");
        setSize(1240, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setJMenuBar(createMenuBar());

        // 1. Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(0, 102, 204));
        panelHeader.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("QUẢN TRỊ LỊCH TRỰC VĂN PHÒNG ĐOÀN THEO TUẦN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        String hoTenUser = (currentUser != null && currentUser.getHoTen() != null)
                ? currentUser.getHoTen() : "Quản trị viên Văn phòng";
        JLabel lblUser = new JLabel("Xin chào: " + hoTenUser);
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(Color.WHITE);

        panelHeader.add(lblTitle, BorderLayout.WEST);
        panelHeader.add(lblUser, BorderLayout.EAST);
        add(panelHeader, BorderLayout.NORTH);

        // 2. Container Panel chính
        JPanel panelMain = new JPanel(new BorderLayout(14, 10));
        panelMain.setBorder(new EmptyBorder(4, 12, 12, 12));

        panelMain.add(createWestPanel(), BorderLayout.WEST);
        panelMain.add(createCenterPanel(), BorderLayout.CENTER);

        add(panelMain, BorderLayout.CENTER);
    }

    // Panel Tác nghiệp bên trái: Căn lề trái đồng bộ, nút bấm đầy đặn
    private JPanel createWestPanel() {
        JPanel pnlWest = new JPanel(new BorderLayout(0, 10));
        pnlWest.setPreferredSize(new Dimension(300, 0));
        pnlWest.setBackground(new Color(248, 250, 252));
        pnlWest.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(190, 200, 210)),
                "Tác nghiệp & Phân công ca trực",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(30, 50, 80)
        ));

        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setOpaque(false);
        pnlContent.setBorder(new EmptyBorder(14, 14, 14, 14));

        lblNgayChon = new JLabel("Ngày trực: (Chưa chọn)");
        lblCaChon = new JLabel("Buổi trực: (Chưa chọn)");
        lblTrangThaiHienTai = new JLabel("Trạng thái: --");

        lblNgayChon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCaChon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTrangThaiHienTai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTrangThaiHienTai.setForeground(new Color(80, 90, 100));

        JLabel lblChonNS = new JLabel("Chọn nhân sự phân công:");
        lblChonNS.setFont(new Font("Segoe UI", Font.BOLD, 12));

        cbChonNhanSu = new JComboBox<>();
        cbChonNhanSu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbChonNhanSu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        btnPhanCong = new JButton("Phân công nhân sự");
        btnHuyPhanCong = new JButton("Hủy phân công ca này");
        btnDongMoCa = new JButton("Đóng / Mở ca trực");

        Font btnFont = new Font("Segoe UI", Font.BOLD, 12);
        Dimension btnSize = new Dimension(Integer.MAX_VALUE, 36);

        JButton[] actionBtns = {btnPhanCong, btnHuyPhanCong, btnDongMoCa};
        for (JButton btn : actionBtns) {
            btn.setFont(btnFont);
            btn.setMaximumSize(btnSize);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setEnabled(false);
        }

        // Ép toàn bộ thành phần căn thẳng lề trái
        lblNgayChon.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblCaChon.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTrangThaiHienTai.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblChonNS.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbChonNhanSu.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContent.add(lblNgayChon);
        pnlContent.add(Box.createVerticalStrut(10));
        pnlContent.add(lblCaChon);
        pnlContent.add(Box.createVerticalStrut(10));
        pnlContent.add(lblTrangThaiHienTai);
        pnlContent.add(Box.createVerticalStrut(22));
        pnlContent.add(lblChonNS);
        pnlContent.add(Box.createVerticalStrut(6));
        pnlContent.add(cbChonNhanSu);
        pnlContent.add(Box.createVerticalStrut(20));
        pnlContent.add(btnPhanCong);
        pnlContent.add(Box.createVerticalStrut(10));
        pnlContent.add(btnHuyPhanCong);
        pnlContent.add(Box.createVerticalStrut(10));
        pnlContent.add(btnDongMoCa);

        pnlWest.add(pnlContent, BorderLayout.NORTH);

        // Nút Khởi tạo 10 ca đặt sát đáy gọn gàng
        JPanel pnlBottomBtn = new JPanel(new BorderLayout());
        pnlBottomBtn.setOpaque(false);
        pnlBottomBtn.setBorder(new EmptyBorder(0, 14, 14, 14));

        btnTaoLichTuan = new JButton("Khởi tạo 10 ca trực cho tuần này");
        btnTaoLichTuan.setPreferredSize(new Dimension(0, 40));
        btnTaoLichTuan.setBackground(new Color(40, 167, 69));
        btnTaoLichTuan.setForeground(Color.WHITE);
        btnTaoLichTuan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTaoLichTuan.setFocusPainted(false);
        pnlBottomBtn.add(btnTaoLichTuan, BorderLayout.CENTER);

        pnlWest.add(pnlBottomBtn, BorderLayout.SOUTH);

        // Sự kiện nút bấm
        btnPhanCong.addActionListener(e -> xuLyPhanCong());
        btnHuyPhanCong.addActionListener(e -> xuLyHuyPhanCong());
        btnDongMoCa.addActionListener(e -> xuLyDongMoCa());
        btnTaoLichTuan.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có muốn khởi tạo tự động 10 ca trực (Sáng/Chiều từ Thứ 2 đến Thứ 6) cho tuần này?",
                    "Xác nhận tạo lịch tuần",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                caTrucDAO.khoiTaoLichTuan(mondayOfWeek);
                loadDataToTimetable();
                JOptionPane.showMessageDialog(this, "Đã khởi tạo thành công 10 ca trực cho tuần!");
            }
        });

        return pnlWest;
    }

    // Panel Bảng lịch trực bên phải: Tự động giãn đầy chiều cao màn hình
    private JPanel createCenterPanel() {
        JPanel panelCenter = new JPanel(new BorderLayout(0, 8));

        // Thanh điều hướng tuần
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 6));
        JButton btnTuanTruoc = new JButton("<< Tuần trước");
        JButton btnTuanSau = new JButton("Tuần tiếp >>");
        JButton btnHienTai = new JButton("Tuần hiện tại");

        Font navFont = new Font("Segoe UI", Font.PLAIN, 12);
        btnTuanTruoc.setFont(navFont);
        btnTuanSau.setFont(navFont);
        btnHienTai.setFont(navFont);
        btnTuanTruoc.setFocusPainted(false);
        btnTuanSau.setFocusPainted(false);
        btnHienTai.setFocusPainted(false);

        lblTuanHienTai = new JLabel();
        lblTuanHienTai.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTuanHienTai.setForeground(new Color(0, 102, 204));
        lblTuanHienTai.setBorder(new EmptyBorder(0, 10, 0, 10));

        pnlNav.add(btnTuanTruoc);
        pnlNav.add(lblTuanHienTai);
        pnlNav.add(btnTuanSau);
        pnlNav.add(btnHienTai);
        panelCenter.add(pnlNav, BorderLayout.NORTH);

        // Khởi tạo bảng ma trận
        timetableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timetable = new JTable(timetableModel);

        timetable.setRowHeight(240);
        timetable.setShowGrid(true);
        timetable.setGridColor(new Color(215, 220, 228));
        timetable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timetable.setCellSelectionEnabled(true);
        timetable.getTableHeader().setReorderingAllowed(false);

        // Header bảng
        timetable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        timetable.getTableHeader().setPreferredSize(new Dimension(0, 38));
        timetable.getTableHeader().setBackground(new Color(238, 242, 247));
        timetable.getTableHeader().setForeground(new Color(30, 50, 80));

        // Renderer từng ô
        timetable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setVerticalAlignment(SwingConstants.CENTER);

                if (column == 0) {
                    c.setBackground(new Color(246, 248, 251));
                } else {
                    if (isSelected) {
                        c.setBackground(new Color(225, 240, 255));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollTable = new JScrollPane(timetable);
        scrollTable.getViewport().setBackground(Color.WHITE);
        scrollTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(190, 200, 210)),
                        "Lịch trực Văn phòng Đoàn (Thứ 2 - Thứ 6): Click chọn 1 ô ca trực để Thao tác / Phân công",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 13),
                        new Color(30, 50, 80)
                ),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        // Tự động chia đều chiều cao 2 hàng Sáng/Chiều theo kích thước cửa sổ
        scrollTable.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int viewportHeight = scrollTable.getViewport().getHeight();
                if (viewportHeight > 120 && timetable.getRowCount() > 0) {
                    timetable.setRowHeight(viewportHeight / timetable.getRowCount());
                }
            }
        });

        panelCenter.add(scrollTable, BorderLayout.CENTER);

        // Sự kiện đổi tuần
        btnTuanTruoc.addActionListener(e -> {
            mondayOfWeek = mondayOfWeek.minusWeeks(1);
            loadDataToTimetable();
        });
        btnTuanSau.addActionListener(e -> {
            mondayOfWeek = mondayOfWeek.plusWeeks(1);
            loadDataToTimetable();
        });
        btnHienTai.addActionListener(e -> {
            mondayOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
            loadDataToTimetable();
        });

        // Click ô
        timetable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedRow = timetable.getSelectedRow();
                selectedCol = timetable.getSelectedColumn();
                hienThiChiTietCaDuocChon();
            }
        });

        return panelCenter;
    }

    private void loadNhanSuToComboBox() {
        cbChonNhanSu.removeAllItems();
        List<NhanSu> list = nhanSuDAO.getAllNhanSu();
        for (NhanSu ns : list) {
            cbChonNhanSu.addItem(ns.getMaSV() + " - " + ns.getHoTen());
        }
    }

    private void loadDataToTimetable() {
        LocalDate fridayOfWeek = mondayOfWeek.plusDays(4);
        lblTuanHienTai.setText("Tuần: " + mondayOfWeek.format(dtf) + "  đến  " + fridayOfWeek.format(dtf));

        String[] headers = {
                "Buổi / Ca",
                "Thứ Hai",
                "Thứ Ba",
                "Thứ Tư",
                "Thứ Năm",
                "Thứ Sáu"
        };

        timetableModel.setDataVector(new Object[][]{
                {"<html><center><b style='font-size:14px; color:#0055AA;'>SÁNG</b><br><i style='font-size:11px; color:#777777;'>(8h - 11h)</i></center></html>", "", "", "", "", ""},
                {"<html><center><b style='font-size:14px; color:#CC5500;'>CHIỀU</b><br><i style='font-size:11px; color:#777777;'>(14h - 17h)</i></center></html>", "", "", "", "", ""}
        }, headers);

        // Khóa độ rộng cột Buổi / Ca để các cột thứ tự chia đều
        timetable.getColumnModel().getColumn(0).setMinWidth(110);
        timetable.getColumnModel().getColumn(0).setMaxWidth(130);
        timetable.getColumnModel().getColumn(0).setPreferredWidth(120);

        // Giữ chiều cao 2 hàng vừa khít khung hiện tại ngay sau khi nạp lại dữ liệu
        if (timetable.getParent() != null && timetable.getParent().getHeight() > 120) {
            timetable.setRowHeight(timetable.getParent().getHeight() / 2);
        }

        caTrucMap.clear();

        List<CaTruc> all = caTrucDAO.getAllCaTruc();
        for (CaTruc ca : all) {
            if (ca.getNgayTruc() == null || ca.getNgayTruc().trim().isEmpty()) continue;
            try {
                LocalDate date = LocalDate.parse(ca.getNgayTruc().trim());
                if (!date.isBefore(mondayOfWeek) && !date.isAfter(fridayOfWeek)) {
                    int col = date.getDayOfWeek().getValue();
                    int row = (ca.getBuoiTruc() != null && ca.getBuoiTruc().equalsIgnoreCase("Sáng")) ? 0 : 1;

                    if (col >= 1 && col <= 5) {
                        String key = row + "_" + col;
                        caTrucMap.put(key, ca);

                        String htmlContent;
                        if (ca.getMaSV() != null && !ca.getMaSV().trim().isEmpty()) {
                            String ten = (ca.getHoTen() != null && !ca.getHoTen().trim().isEmpty()) ? ca.getHoTen() : "";
                            htmlContent = "<html><center>"
                                    + "<b style='color:#CC0000; font-size:13px;'>[Đã có người đăng ký]</b><br><br>"
                                    + "<b style='color:#003366; font-size:12px;'>" + ten + "</b><br>"
                                    + "<span style='color:#666666; font-size:11px;'>Mã SV: " + ca.getMaSV() + "</span>"
                                    + "</center></html>";
                        } else if ("Không trực".equalsIgnoreCase(ca.getTrangThai())) {
                            htmlContent = "<html><center><i style='color:#888888; font-size:12px;'>[Đã đóng ca]</i></center></html>";
                        } else {
                            htmlContent = "<html><center><b style='color:#0055CC; font-size:13px;'>[Mở đăng ký]</b></center></html>";
                        }
                        timetableModel.setValueAt(htmlContent, row, col);
                    }
                }
            } catch (Exception ignored) {}
        }

        resetPanelChiTiet();
    }

    private void hienThiChiTietCaDuocChon() {
        if (selectedCol <= 0 || selectedRow < 0) {
            resetPanelChiTiet();
            return;
        }

        LocalDate ngay = mondayOfWeek.plusDays(selectedCol - 1);
        String buoi = (selectedRow == 0) ? "Sáng" : "Chiều";
        lblNgayChon.setText("Ngày trực: " + ngay.format(dtf));
        lblCaChon.setText("Buổi trực: " + buoi + (selectedRow == 0 ? " (8h - 11h)" : " (14h - 17h)"));

        String key = selectedRow + "_" + selectedCol;
        selectedCaTruc = caTrucMap.get(key);

        if (selectedCaTruc != null) {
            lblTrangThaiHienTai.setText("Trạng thái: " + selectedCaTruc.getTrangThai());
            btnPhanCong.setEnabled(true);
            btnDongMoCa.setEnabled(true);

            if (selectedCaTruc.getMaSV() != null && !selectedCaTruc.getMaSV().trim().isEmpty()) {
                btnHuyPhanCong.setEnabled(true);
                for (int i = 0; i < cbChonNhanSu.getItemCount(); i++) {
                    if (cbChonNhanSu.getItemAt(i).startsWith(selectedCaTruc.getMaSV())) {
                        cbChonNhanSu.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                btnHuyPhanCong.setEnabled(false);
            }

            if ("Không trực".equalsIgnoreCase(selectedCaTruc.getTrangThai())) {
                btnDongMoCa.setText("Mở lại ca trực");
            } else {
                btnDongMoCa.setText("Đóng ca này");
            }
        } else {
            lblTrangThaiHienTai.setText("Trạng thái: Chưa tạo ca");
            btnPhanCong.setEnabled(false);
            btnHuyPhanCong.setEnabled(false);
            btnDongMoCa.setEnabled(false);
        }
    }

    private void resetPanelChiTiet() {
        selectedCaTruc = null;
        selectedRow = -1;
        selectedCol = -1;
        lblNgayChon.setText("Ngày trực: (Chưa chọn)");
        lblCaChon.setText("Buổi trực: (Chưa chọn)");
        lblTrangThaiHienTai.setText("Trạng thái: --");
        btnPhanCong.setEnabled(false);
        btnHuyPhanCong.setEnabled(false);
        btnDongMoCa.setEnabled(false);
    }

    private void xuLyPhanCong() {
        if (selectedCaTruc == null) return;

        String item = (String) cbChonNhanSu.getSelectedItem();
        if (item == null || item.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân sự để phân công!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] parts = item.split(" - ");
        String maSV = parts[0].trim();
        String hoTen = parts.length > 1 ? parts[1].trim() : "";

        selectedCaTruc.setMaSV(maSV);
        selectedCaTruc.setHoTen(hoTen);
        selectedCaTruc.setTrangThai("Đã có người đăng ký");

        if (caTrucDAO.update(selectedCaTruc)) {
            JOptionPane.showMessageDialog(this, "Phân công thành công cho sinh viên: " + hoTen);
            loadDataToTimetable();
        } else {
            JOptionPane.showMessageDialog(this, "Phân công thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyPhanCong() {
        if (selectedCaTruc == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn hủy phân công sinh viên trong ca trực này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            selectedCaTruc.setMaSV(null);
            selectedCaTruc.setHoTen(null);
            selectedCaTruc.setTrangThai("Mở đăng ký");

            if (caTrucDAO.update(selectedCaTruc)) {
                JOptionPane.showMessageDialog(this, "Đã hủy phân công ca trực!");
                loadDataToTimetable();
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyDongMoCa() {
        if (selectedCaTruc == null) return;

        boolean isCurrentlyClosed = "Không trực".equalsIgnoreCase(selectedCaTruc.getTrangThai());
        String newStatus = isCurrentlyClosed ? "Mở đăng ký" : "Không trực";

        selectedCaTruc.setTrangThai(newStatus);
        if ("Không trực".equals(newStatus)) {
            selectedCaTruc.setMaSV(null);
            selectedCaTruc.setHoTen(null);
        }

        if (caTrucDAO.update(selectedCaTruc)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái ca trực thành: " + newStatus);
            loadDataToTimetable();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

        JMenu menuQuanLy = new JMenu("Quản lý Chức năng");
        menuNhanSu = new JMenuItem("Quản lý Nhân sự");
        menuCaTruc = new JMenuItem("Quản lý Ca trực");
        menuSuKien = new JMenuItem("Quản lý Sự kiện");
        menuTruyenThong = new JMenuItem("Quản lý Truyền thông");

        menuNhanSu.addActionListener(e -> {
            new NhanSuView(currentUser).setVisible(true);
            this.dispose();
        });

        menuCaTruc.addActionListener(e -> loadDataToTimetable());

        menuSuKien.addActionListener(e -> {
            new SuKienView(currentUser).setVisible(true);
            this.dispose();
        });

        menuTruyenThong.addActionListener(e -> {
            new TruyenThongView(currentUser).setVisible(true);
            this.dispose();
        });

        menuQuanLy.add(menuNhanSu);
        menuQuanLy.add(menuCaTruc);
        menuQuanLy.add(menuSuKien);
        menuQuanLy.add(menuTruyenThong);

        menuBar.add(menuHeThong);
        menuBar.add(menuQuanLy);
        return menuBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CaTrucView(null).setVisible(true));
    }
}