package com.vanphongdoan.view.user;

import com.vanphongdoan.dao.CaTrucDAO;
import com.vanphongdoan.model.CaTruc;
import com.vanphongdoan.model.NhanSu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DangKyCaTrucDialog extends JDialog {
    private CaTrucDAO caTrucDAO = new CaTrucDAO();
    private NhanSu currentUser;
    private JTable tableLichTruc;
    private DefaultTableModel tableModel;
    
    private CaTruc[][] matrixCaTruc = new CaTruc[2][5];

    public DangKyCaTrucDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "LỊCH TRỰC VĂN PHÒNG ĐOÀN - ĐĂNG KÝ & XIN HỦY CA", true);
        this.currentUser = currentUser;
        setSize(1350, 580); 
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(12, 12));

        // --- 1. HEADER HƯỚNG DẪN ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBorder(new EmptyBorder(15, 18, 8, 18));
        JLabel lblHuongDan = new JLabel("<html><b>Lịch trực Văn phòng Đoàn (Thứ 2 - Thứ 6):</b> Click chọn <b>đúng 1 ô ca trực</b> rồi bấm nút để Đăng ký hoặc Gửi yêu cầu xin hủy.</html>");
        lblHuongDan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlHeader.add(lblHuongDan, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. BẢNG DẠNG LƯỚI ---
        String[] cols = {"Buổi / Ca", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableLichTruc = new JTable(tableModel);
        tableLichTruc.setRowHeight(180); 
        tableLichTruc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        tableLichTruc.setShowGrid(true);
        tableLichTruc.setGridColor(new Color(210, 210, 210));

        tableLichTruc.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableLichTruc.getTableHeader().setBackground(new Color(230, 240, 250));
        tableLichTruc.getTableHeader().setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) tableLichTruc.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tableLichTruc.setCellSelectionEnabled(true);
        tableLichTruc.setRowSelectionAllowed(false);
        tableLichTruc.setColumnSelectionAllowed(false);
        tableLichTruc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < cols.length; i++) {
            tableLichTruc.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        tableLichTruc.getColumnModel().getColumn(0).setPreferredWidth(130);
        tableLichTruc.getColumnModel().getColumn(0).setMaxWidth(130);

        loadDataToGrid();

        // --- 3. FOOTER NÚT BẤM TO RÕ ---
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 15));
        
        JButton btnXinHuy = new JButton("Gửi yêu cầu xin hủy ca");
        btnXinHuy.setBackground(new Color(204, 102, 0));
        btnXinHuy.setForeground(Color.WHITE);
        btnXinHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXinHuy.setPreferredSize(new Dimension(190, 38));
        btnXinHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnDangKy = new JButton("Đăng ký Ca trực đã chọn");
        btnDangKy.setBackground(new Color(0, 102, 204));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDangKy.setPreferredSize(new Dimension(210, 38));
        btnDangKy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Xử lý Đăng ký
        btnDangKy.addActionListener(e -> {
            int row = tableLichTruc.getSelectedRow();
            int col = tableLichTruc.getSelectedColumn();

            if (row == -1 || col <= 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn chính xác 1 ô ca trực trong bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CaTruc targetCa = matrixCaTruc[row][col - 1];
            if (targetCa == null) {
                JOptionPane.showMessageDialog(this, "Ô này hiện chưa được khởi tạo dữ liệu trong CSDL!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cellValue = (String) tableModel.getValueAt(row, col);
            if (cellValue == null || cellValue.contains("Chưa mở")) {
                JOptionPane.showMessageDialog(this, "Ca trực này hiện chưa mở!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cellValue.contains("[Đã đăng ký bởi bạn]")) {

    public DangKyCaTrucDialog(JFrame parent, NhanSu currentUser) {
        super(parent, "ĐĂNG KÝ CA TRỰC VĂN PHÒNG ĐOÀN", true);
        this.currentUser = currentUser;
        setSize(760, 440);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        String[] cols = {"Mã Ca", "Buổi trực", "Ngày trực", "Thứ", "Trạng thái", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);

        // Load dữ liệu
        List<CaTruc> list = caTrucDAO.getAllCaTruc();
        for (CaTruc ct : list) {
            boolean isMine = currentUser.getMaSV().equalsIgnoreCase(ct.getMaSV());
            if ("Mở đăng ký".equalsIgnoreCase(ct.getTrangThai()) || isMine) {
                model.addRow(new Object[]{
                    ct.getIdCa(), ct.getBuoiTruc(), ct.getNgayTruc(), ct.getThu(),
                    isMine ? "Đã đăng ký" : ct.getTrangThai(), ct.getGhiChu() != null ? ct.getGhiChu() : ""
                });
            }
        }

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnDangKy = new JButton("Xác nhận Đăng ký ca chọn");
        btnDangKy.setBackground(new Color(0, 102, 204));
        btnDangKy.setForeground(Color.WHITE);
        btnDangKy.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnDangKy.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một ca trực từ danh sách!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idCa = Integer.parseInt(model.getValueAt(row, 0).toString());
            String buoiTruc = model.getValueAt(row, 1).toString();
            String ngayTruc = model.getValueAt(row, 2).toString();
            String thu = model.getValueAt(row, 3).toString();
            String status = model.getValueAt(row, 4).toString();
            String ghiChu = model.getValueAt(row, 5).toString();

            if ("Đã đăng ký".equals(status)) {
                JOptionPane.showMessageDialog(this, "Bạn đã đăng ký ca trực này rồi!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (cellValue.contains("BĐH")) {
                JOptionPane.showMessageDialog(this, "Ca trực này đang chờ Ban Điều Hành xử lý yêu cầu hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cellValue.contains("[Đã có người đăng ký]") || (targetCa.getMaSV() != null && !targetCa.getMaSV().trim().isEmpty())) {
                JOptionPane.showMessageDialog(this, "Ca trực này đã có thành viên khác nhận!", "Từ chối", JOptionPane.WARNING_MESSAGE);
                return;
            }

            targetCa.setMaSV(currentUser.getMaSV());
            targetCa.setHoTen(currentUser.getHoTen());
            targetCa.setTrangThai("Đã phân công");

            if (caTrucDAO.update(targetCa)) {
                String buoiStr = (row == 0) ? "Sáng" : "Chiều";
                String thuStr = getThuString(col);
                JOptionPane.showMessageDialog(this, "Đăng ký thành công ca " + buoiStr + " (" + thuStr + ")!");
                loadDataToGrid();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Xử lý Xin hủy đăng ký
        btnXinHuy.addActionListener(e -> {
            int row = tableLichTruc.getSelectedRow();
            int col = tableLichTruc.getSelectedColumn();

            if (row == -1 || col <= 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn chính xác 1 ô ca trực bạn muốn xin hủy!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CaTruc targetCa = matrixCaTruc[row][col - 1];
            if (targetCa == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu ca trực tương ứng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cellValue = (String) tableModel.getValueAt(row, col);
            if (cellValue == null || !cellValue.contains("[Đã đăng ký bởi bạn]")) {
                JOptionPane.showMessageDialog(this, "Bạn chỉ có thể gửi yêu cầu xin hủy những ca do chính bạn đã đăng ký!", "Từ chối", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String buoiStr = (row == 0) ? "Sáng" : "Chiều";
            String thuStr = getThuString(col);

            XinHuyCaTrucDialog dialog = new XinHuyCaTrucDialog((JFrame) getParent(), buoiStr, thuStr);
            dialog.setVisible(true);

            String lyDo = dialog.getLyDo();
            String tenAnh = dialog.getTenAnhMinhChung();

            if (lyDo != null) {
                targetCa.setTrangThai("Chờ hủy");
                
                String oldGhiChu = targetCa.getGhiChu() != null ? targetCa.getGhiChu() : "";
                String minhChung = tenAnh.isEmpty() ? "" : " | 📎 Ảnh đính kèm: " + tenAnh;
                targetCa.setGhiChu(oldGhiChu + " [Xin hủy: " + lyDo + minhChung + "]");

                if (caTrucDAO.update(targetCa)) {
                    JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu xin hủy ca thành công!\nVui lòng đợi Ban Điều Hành xét duyệt.");
                    loadDataToGrid();
                } else {
                    JOptionPane.showMessageDialog(this, "Gửi yêu cầu thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlBottom.add(btnXinHuy);
        pnlBottom.add(btnDangKy);
        add(new JScrollPane(tableLichTruc), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadDataToGrid() {
        tableModel.setRowCount(0);
        matrixCaTruc = new CaTruc[2][5];

        List<CaTruc> list = caTrucDAO.getAllCaTruc();

        // Thêm khung giờ Sáng (8h - 11h) và Chiều (14h - 17h) vào cột đầu tiên
        String[] rowSang = {"<html><center><font size='4' color='#003366'><b>☀ SÁNG</b></font><br><font size='2.5' color='#555555'><i>(8h - 11h)</i></font></center></html>", 
                            "<html><font color='gray'>Chưa mở</font></html>", "<html><font color='gray'>Chưa mở</font></html>", "<html><font color='gray'>Chưa mở</font></html>", "<html><font color='gray'>Chưa mở</font></html>", "<html><font color='gray'>Chưa mở</font></html>"};
        String[] rowChieu = {"<html><center><font size='4' color='#993300'><b>🌙 CHIỀU</b></font><br><font size='2.5' color='#555555'><i>(14h - 17h)</i></font></center></html>", 
                             "<html><font color='gray'>Chưa mở</font></html>", "<html><font color='gray'>Chưa mở</font></html>", "<html><font color='gray'>Chưa mở</font></html>", "<html><font color='gray'>Chưa mở</font></html>", "<html><font color='gray'>Chưa mở</font></html>"};

        for (CaTruc ct : list) {
            int colIndex = getColumnIndexByThu(ct.getThu());
            if (colIndex != -1) {
                int rowIndex = (ct.getBuoiTruc() != null && ct.getBuoiTruc().equalsIgnoreCase("Sáng")) ? 0 : 1;
                matrixCaTruc[rowIndex][colIndex - 1] = ct;

                String cellText = formatCellContent(ct);
                if (rowIndex == 0) {
                    rowSang[colIndex] = cellText;
                } else {
                    rowChieu[colIndex] = cellText;
                }
            }
        }

        tableModel.addRow(rowSang);
        tableModel.addRow(rowChieu);
    }

    private String formatCellContent(CaTruc ct) {
        String trangThai = ct.getTrangThai() != null ? ct.getTrangThai() : "";
        String maSVNhan = ct.getMaSV() != null ? ct.getMaSV().trim() : "";
        String hoTenNhan = ct.getHoTen() != null ? ct.getHoTen().trim() : "";
        String ghiChu = ct.getGhiChu() != null ? ct.getGhiChu() : "Trực văn phòng";

        if (hoTenNhan.contains("(")) {
            hoTenNhan = hoTenNhan.substring(0, hoTenNhan.indexOf("(")).trim();
        }

        if ("Chờ hủy".equalsIgnoreCase(trangThai) && maSVNhan.equalsIgnoreCase(currentUser.getMaSV().trim())) {
            return "<html><center><font size='4' color='#cc6600'><b>[Đang chờ<br>BĐH&nbsp;duyệt&nbsp;hủy]</b></font><br><br>"
                 + "<font size='3' color='#333333'>👤 " + hoTenNhan + "</font></center></html>";
        } else if (!maSVNhan.isEmpty() && maSVNhan.equalsIgnoreCase(currentUser.getMaSV().trim())) {
            return "<html><center><font size='4' color='#008000'><b>[Đã đăng ký bởi bạn]</b></font><br><br>"
                 + "<font size='3' color='#333333'>👤 " + hoTenNhan + "</font></center></html>";
        } else if ("Đã phân công".equalsIgnoreCase(trangThai) || !maSVNhan.isEmpty()) {
            return "<html><center><font size='4' color='#cc0000'><b>[Đã có người đăng ký]</b></font><br><br>"
                 + "<font size='3' color='#333333'>👤 " + hoTenNhan + "</font></center></html>";
        } else {
            return "<html><center><font size='4' color='#0044cc'><b>[Mở đăng ký]</b></font><br><br>"
                 + "<font size='3' color='#0055a4'><b>📝 " + ghiChu + "</b></font></center></html>";
        }
    }

    private int getColumnIndexByThu(String thu) {
        if (thu == null) return -1;
        String t = thu.trim().toLowerCase();
        if (t.contains("2") || t.contains("hai")) return 1;
        if (t.contains("3") || t.contains("ba")) return 2;
        if (t.contains("4") || t.contains("tư")) return 3;
        if (t.contains("5") || t.contains("năm")) return 4;
        if (t.contains("6") || t.contains("sáu")) return 5;
        return -1;
    }
    
    private String getThuString(int col) {
        switch (col) {
            case 1: return "Thứ 2";
            case 2: return "Thứ 3";
            case 3: return "Thứ 4";
            case 4: return "Thứ 5";
            case 5: return "Thứ 6";
            default: return "";
        }
    }
    // --- HÀM MAIN TẠM ĐỂ TEST TRỰC TIẾP GIAO DIỆN ---
// --- HÀM MAIN TẠM ĐỂ TEST TRỰC TIẾP GIAO DIỆN ---
    public static void main(String[] args) {
        // Thiết lập giao diện theo hệ thống cho đẹp
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame dummyFrame = new JFrame(); // Khung cha giả lập
            NhanSu dummyUser = new NhanSu();  // Tạo user giả lập test
            dummyUser.setMaSV("TV01");
            dummyUser.setHoTen("Trần Thị B");
            
            DangKyCaTrucDialog dialog = new DangKyCaTrucDialog(dummyFrame, dummyUser);
            dialog.setVisible(true);
            System.exit(0); // Thoát chương trình khi đóng dialog
        });
    }
}
            CaTruc ct = new CaTruc();
            ct.setIdCa(idCa);
            ct.setBuoiTruc(buoiTruc);
            ct.setNgayTruc(ngayTruc);
            ct.setThu(thu);
            ct.setMaSV(currentUser.getMaSV());
            ct.setHoTen(currentUser.getHoTen());
            ct.setTrangThai("Đã phân công");
            ct.setGhiChu(ghiChu);

            if (caTrucDAO.update(ct)) {
                JOptionPane.showMessageDialog(this, "Đăng ký ca trực thành công!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đăng ký thất bại! Vui lòng thử lại sau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlBottom.add(btnDangKy);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }
}
