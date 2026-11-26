package com.vanphongdoan.view.user;

import com.vanphongdoan.dao.SuKienDAO;
import com.vanphongdoan.model.NhanSu;
import com.vanphongdoan.model.SuKien;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class XinHuySuKienDialog extends JDialog {
    private JTextArea txtLyDo;
    private JLabel lblFileStatus;
    private File selectedFile;
    private boolean isSubmitted = false;

    public XinHuySuKienDialog(JDialog parent, SuKien suKien, NhanSu currentUser, SuKienDAO suKienDAO) {
        super(parent, "GỬI YÊU CẦU XIN HỦY ĐĂNG KÝ SỰ KIỆN", true);
        setSize(520, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(12, 12));

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblEventName = new JLabel("<html>Sự kiện: <b>" + suKien.getTenSuKien() + "</b> (Vị trí: " + suKien.getViTriDangKy() + ")</html>");
        lblEventName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEventName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLyDoHeader = new JLabel("Lý do xin hủy (Bắt buộc):");
        lblLyDoHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLyDoHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLyDo = new JTextArea(4, 30);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);
        scrollLyDo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblProofHeader = new JLabel("Ảnh minh chứng (Lý do sức khỏe / Lịch trùng...):");
        lblProofHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblProofHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlFile = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        pnlFile.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnChooseFile = new JButton("📎 Chọn ảnh minh chứng");
        btnChooseFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFileStatus = new JLabel(" Chưa chọn tệp nào");
        lblFileStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFileStatus.setForeground(Color.GRAY);

        btnChooseFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                lblFileStatus.setText(" " + selectedFile.getName());
                lblFileStatus.setForeground(new Color(0, 128, 0));
            }
        });

        pnlFile.add(btnChooseFile);
        pnlFile.add(lblFileStatus);

        pnlMain.add(lblEventName);
        pnlMain.add(Box.createVerticalStrut(12));
        pnlMain.add(lblLyDoHeader);
        pnlMain.add(Box.createVerticalStrut(5));
        pnlMain.add(scrollLyDo);
        pnlMain.add(Box.createVerticalStrut(12));
        pnlMain.add(lblProofHeader);
        pnlMain.add(pnlFile);

        add(pnlMain, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        JButton btnCancel = new JButton("Đóng");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSubmit = new JButton("Gửi yêu cầu BĐH");
        btnSubmit.setBackground(new Color(204, 102, 0));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnSubmit.addActionListener(e -> {
            String lyDo = txtLyDo.getText().trim();
            if (lyDo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do xin hủy sự kiện!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ảnh minh chứng đính kèm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = suKienDAO.xinHuySuKien(currentUser.getMaSV(), suKien.getIdSuKien(), lyDo, selectedFile.getAbsolutePath());
            if (success) {
                JOptionPane.showMessageDialog(this, "Đã gửi yêu cầu xin hủy đến Ban Điều Hành thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                isSubmitted = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Gửi yêu cầu thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlBottom.add(btnCancel);
        pnlBottom.add(btnSubmit);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }
}