package com.vanphongdoan.view.admin;

import com.vanphongdoan.dao.NhanSuDAO;
import com.vanphongdoan.model.PhanQuyen;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PhanQuyenDialog extends JDialog {

    private String tenDangNhap;
    private String hoTen;
    private NhanSuDAO nhanSuDAO = new NhanSuDAO();

    private JCheckBox chkDoiNgoai;
    private JCheckBox chkSuKien;
    private JCheckBox chkTruyenThong;
    private JCheckBox chkXuatBaoCao;
    private JButton btnLuu;
    private JButton btnHuy;

    public PhanQuyenDialog(JFrame parent, String tenDangNhap, String hoTen) {
        super(parent, "PHÂN QUYỀN TÁC NGHIỆP BAN ĐIỀU HÀNH", true);
        this.tenDangNhap = tenDangNhap;
        this.hoTen = hoTen;

        setSize(430, 320);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadQuyenHienTai();
    }

    private void initUI() {
        // Tiêu đề
        JPanel pnlTop = new JPanel(new GridLayout(2, 1, 3, 3));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        JLabel lblUser = new JLabel("Cấp quyền cho: " + hoTen);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(new Color(0, 102, 204));
        JLabel lblUsername = new JLabel("Tài khoản: " + tenDangNhap);
        lblUsername.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        pnlTop.add(lblUser);
        pnlTop.add(lblUsername);
        add(pnlTop, BorderLayout.NORTH);

        // Danh sách quyền hạn
        JPanel pnlCenter = new JPanel(new GridLayout(4, 1, 8, 8));
        pnlCenter.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Danh mục quyền hạn được phép thao tác",
                TitledBorder.LEFT, TitledBorder.TOP
        ));

        chkDoiNgoai = new JCheckBox("Quản lý Lịch trực tuần & Duyệt sổ nhật ký trực (Đối ngoại)");
        chkSuKien = new JCheckBox("Tạo Sự kiện, chốt danh sách & Duyệt minh chứng (Sự kiện)");
        chkTruyenThong = new JCheckBox("Giao nhiệm vụ truyền thông & Kiểm duyệt bài viết/ấn phẩm");
        chkXuatBaoCao = new JCheckBox("Xem bảng thống kê & Xuất báo cáo thi đua của Ban");

        pnlCenter.add(chkDoiNgoai);
        pnlCenter.add(chkSuKien);
        pnlCenter.add(chkTruyenThong);
        pnlCenter.add(chkXuatBaoCao);
        add(pnlCenter, BorderLayout.CENTER);

        // Nút lưu / đóng
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnLuu = new JButton("Lưu phân quyền");
        btnHuy = new JButton("Đóng");

        btnLuu.addActionListener(e -> xuLyLuuQuyen());
        btnHuy.addActionListener(e -> dispose());

        pnlBottom.add(btnLuu);
        pnlBottom.add(btnHuy);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadQuyenHienTai() {
        PhanQuyen pq = nhanSuDAO.getPhanQuyenByUsername(tenDangNhap);
        if (pq != null) {
            chkDoiNgoai.setSelected(pq.isQuyenDoiNgoai());
            chkSuKien.setSelected(pq.isQuyenSuKien());
            chkTruyenThong.setSelected(pq.isQuyenTruyenThong());
            chkXuatBaoCao.setSelected(pq.isQuyenXuatBaoCao());
        }
    }

    private void xuLyLuuQuyen() {
        PhanQuyen pq = new PhanQuyen(
                tenDangNhap,
                chkDoiNgoai.isSelected(),
                chkSuKien.isSelected(),
                chkTruyenThong.isSelected(),
                chkXuatBaoCao.isSelected()
        );

        if (nhanSuDAO.savePhanQuyen(pq)) {
            JOptionPane.showMessageDialog(this, "Đã lưu phân quyền chi tiết cho Ban Điều hành!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu phân quyền thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}