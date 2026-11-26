package com.vanphongdoan.view.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class XinHuyCaTrucDialog extends JDialog {
    private String lyDo = null;
    private String tenAnhMinhChung = "";
    private JTextArea txtLyDo;
    private JLabel lblTenAnh;
    private JLabel lblPreview;

    public XinHuyCaTrucDialog(JFrame parent, String buoiStr, String thuStr) {
        super(parent, "Lý do xin hủy ca trực", true);
        setSize(520, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlContent = new JPanel(new BorderLayout(5, 10));
        pnlContent.setBorder(new EmptyBorder(15, 15, 5, 15));
        
        JLabel lblTieuDe = new JLabel("Nhập lý do xin hủy ca " + buoiStr + " (" + thuStr + "):");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtLyDo = new JTextArea(4, 20);
        txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);

        // Panel đính kèm và Preview ảnh minh chứng
        JPanel pnlAttachment = new JPanel(new BorderLayout(5, 5));
        JPanel pnlUploadBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton btnChonAnh = new JButton("📎 Đính kèm ảnh minh chứng");
        btnChonAnh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        lblTenAnh = new JLabel(" Chưa có ảnh nào");
        lblTenAnh.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTenAnh.setForeground(Color.GRAY);
        
        pnlUploadBar.add(btnChonAnh);
        pnlUploadBar.add(lblTenAnh);

        lblPreview = new JLabel("Khu vực hiển thị ảnh minh chứng", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(480, 200));
        lblPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        lblPreview.setForeground(Color.GRAY);

        pnlAttachment.add(pnlUploadBar, BorderLayout.NORTH);
        pnlAttachment.add(lblPreview, BorderLayout.CENTER);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.add(scrollLyDo, BorderLayout.NORTH);
        pnlCenter.add(pnlAttachment, BorderLayout.CENTER);

        pnlContent.add(lblTieuDe, BorderLayout.NORTH);
        pnlContent.add(pnlCenter, BorderLayout.CENTER);

        // Bắt sự kiện chọn file ảnh
        btnChonAnh.addActionListener(ev -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn ảnh minh chứng");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh (JPG, PNG, JPEG)", "jpg", "png", "jpeg");
            fileChooser.setFileFilter(filter);
            
            int userSelection = fileChooser.showOpenDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToUpload = fileChooser.getSelectedFile();
                tenAnhMinhChung = fileToUpload.getName();
                lblTenAnh.setText(" " + tenAnhMinhChung);
                lblTenAnh.setForeground(new Color(0, 102, 204));
                
                try {
                    ImageIcon icon = new ImageIcon(fileToUpload.getAbsolutePath());
                    Image img = icon.getImage();
                    double ratio = Math.min(480.0 / img.getWidth(null), 200.0 / img.getHeight(null));
                    int newWidth = (int) (img.getWidth(null) * ratio);
                    int newHeight = (int) (img.getHeight(null) * ratio);
                    
                    Image scaledImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    lblPreview.setText("");
                    lblPreview.setIcon(new ImageIcon(scaledImg));
                } catch (Exception ex) {
                    lblPreview.setText("Lỗi hiển thị ảnh!");
                }
            }
        });

        // Nút xác nhận & hủy bỏ
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnXacNhan = new JButton("Xác nhận gửi BĐH");
        btnXacNhan.setBackground(new Color(0, 102, 204));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnHuyBo = new JButton("Hủy bỏ");
        btnHuyBo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnXacNhan.addActionListener(ev -> {
            String text = txtLyDo.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do xin hủy!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            lyDo = text;
            dispose();
        });

        btnHuyBo.addActionListener(ev -> dispose());

        pnlBtns.add(btnXacNhan);
        pnlBtns.add(btnHuyBo);

        add(pnlContent, BorderLayout.CENTER);
        add(pnlBtns, BorderLayout.SOUTH);
    }

    public String getLyDo() { return lyDo; }
    public String getTenAnhMinhChung() { return tenAnhMinhChung; }
}