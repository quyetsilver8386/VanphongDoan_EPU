-- 1. Tạo Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'QL_VanPhongDoan')
BEGIN
    CREATE DATABASE QL_VanPhongDoan;
END
GO

USE QL_VanPhongDoan;
GO

-- 2. Xóa bảng cũ nếu đã tồn tại (theo thứ tự quan hệ khóa ngoại để tránh lỗi)
IF OBJECT_ID('PhanQuyenBanDieuHanh', 'U') IS NOT NULL DROP TABLE PhanQuyenBanDieuHanh;
IF OBJECT_ID('ChiTietSuKien', 'U') IS NOT NULL DROP TABLE ChiTietSuKien;
IF OBJECT_ID('ChiTietCaTruc', 'U') IS NOT NULL DROP TABLE ChiTietCaTruc;
IF OBJECT_ID('NhiemVuTruyenThong', 'U') IS NOT NULL DROP TABLE NhiemVuTruyenThong;
IF OBJECT_ID('SuKien', 'U') IS NOT NULL DROP TABLE SuKien;
IF OBJECT_ID('CaTruc', 'U') IS NOT NULL DROP TABLE CaTruc;
IF OBJECT_ID('NhanSu', 'U') IS NOT NULL DROP TABLE NhanSu;
IF OBJECT_ID('BanChuyenMon', 'U') IS NOT NULL DROP TABLE BanChuyenMon;
IF OBJECT_ID('VaiTro', 'U') IS NOT NULL DROP TABLE VaiTro;
GO

-- 3. Tạo các bảng danh mục (Bảng cha)
CREATE TABLE VaiTro (
    ID_VaiTro INT IDENTITY(1,1) PRIMARY KEY,
    TenVaiTro NVARCHAR(50) NOT NULL UNIQUE
);
GO

CREATE TABLE BanChuyenMon (
    ID_Ban INT IDENTITY(1,1) PRIMARY KEY,
    TenBan NVARCHAR(50) NOT NULL UNIQUE
);
GO

-- 4. Tạo bảng Nhân sự (Tài khoản người dùng)
CREATE TABLE NhanSu (
    MaSV VARCHAR(20) PRIMARY KEY,
    MatKhau VARCHAR(255) NOT NULL DEFAULT '123456',
    HoTen NVARCHAR(100) NOT NULL,
    Lop NVARCHAR(50),
    SoDienThoai VARCHAR(15),
    TrangThai BIT NOT NULL DEFAULT 1, -- 1: Hoạt động, 0: Tạm khóa
    ID_VaiTro INT NOT NULL,
    ID_Ban INT NULL,
    CONSTRAINT FK_NhanSu_VaiTro FOREIGN KEY (ID_VaiTro) REFERENCES VaiTro(ID_VaiTro),
    CONSTRAINT FK_NhanSu_BanChuyenMon FOREIGN KEY (ID_Ban) REFERENCES BanChuyenMon(ID_Ban)
);
GO

-- 5. Bảng Phân quyền nâng cao cho Ban Điều hành
CREATE TABLE PhanQuyenBanDieuHanh (
    TenDangNhap VARCHAR(20) PRIMARY KEY,
    QuyenDoiNgoai BIT NOT NULL DEFAULT 0,    -- Quản lý Ca trực
    QuyenSuKien BIT NOT NULL DEFAULT 0,      -- Quản lý Sự kiện
    QuyenTruyenThong BIT NOT NULL DEFAULT 0, -- Quản lý Truyền thông
    QuyenXuatBaoCao BIT NOT NULL DEFAULT 0,  -- Báo cáo thống kê
    CONSTRAINT FK_PhanQuyen_NhanSu FOREIGN KEY (TenDangNhap) REFERENCES NhanSu(MaSV) ON DELETE CASCADE
);
GO

-- 6. Tạo bảng Quản lý ca trực
CREATE TABLE CaTruc (
    ID_CaTruc INT IDENTITY(1,1) PRIMARY KEY,
    NgayTruc DATE NOT NULL,
    BuoiTruc NVARCHAR(20) NOT NULL,
    SoLuongToiDa INT NOT NULL DEFAULT 4,
    CONSTRAINT CK_CaTruc_BuoiTruc CHECK (BuoiTruc IN (N'Sáng', N'Chiều'))
);
GO

CREATE TABLE ChiTietCaTruc (
    ID_CaTruc INT NOT NULL,
    MaSV VARCHAR(20) NOT NULL,
    TrangThaiDiemDanh NVARCHAR(50) NOT NULL DEFAULT N'Chưa điểm danh',
    MinhChung NVARCHAR(MAX) NULL,
    CONSTRAINT PK_ChiTietCaTruc PRIMARY KEY (ID_CaTruc, MaSV),
    CONSTRAINT FK_ChiTietCaTruc_CaTruc FOREIGN KEY (ID_CaTruc) REFERENCES CaTruc(ID_CaTruc) ON DELETE CASCADE,
    CONSTRAINT FK_ChiTietCaTruc_NhanSu FOREIGN KEY (MaSV) REFERENCES NhanSu(MaSV) ON DELETE CASCADE
);
GO

-- 7. Tạo bảng Quản lý sự kiện
CREATE TABLE SuKien (
    ID_SuKien INT IDENTITY(1,1) PRIMARY KEY,
    TenSuKien NVARCHAR(200) NOT NULL,
    NgayToChuc DATE NOT NULL,
    DiaDiem NVARCHAR(200),
    KinhPhi DECIMAL(18,0) DEFAULT 0,
    TrangThai NVARCHAR(50) NOT NULL DEFAULT N'Đang diễn ra'
);
GO

CREATE TABLE ChiTietSuKien (
    ID_SuKien INT NOT NULL,
    MaSV VARCHAR(20) NOT NULL,
    ViTriHoTro NVARCHAR(100) NULL,
    TrangThaiDuyet NVARCHAR(50) NOT NULL DEFAULT N'Chờ duyệt',
    CONSTRAINT PK_ChiTietSuKien PRIMARY KEY (ID_SuKien, MaSV),
    CONSTRAINT FK_ChiTietSuKien_SuKien FOREIGN KEY (ID_SuKien) REFERENCES SuKien(ID_SuKien) ON DELETE CASCADE,
    CONSTRAINT FK_ChiTietSuKien_NhanSu FOREIGN KEY (MaSV) REFERENCES NhanSu(MaSV) ON DELETE CASCADE
);
GO

-- 8. Tạo bảng Quản lý nhiệm vụ truyền thông
CREATE TABLE NhiemVuTruyenThong (
    ID_NhiemVu INT IDENTITY(1,1) PRIMARY KEY,
    TenNhiemVu NVARCHAR(200) NOT NULL,
    MaSV_DuocGiao VARCHAR(20) NULL,
    HanChot DATE NULL,
    TrangThai NVARCHAR(50) NOT NULL DEFAULT N'Đang thực hiện',
    CONSTRAINT FK_NhiemVu_NhanSu FOREIGN KEY (MaSV_DuocGiao) REFERENCES NhanSu(MaSV) ON DELETE SET NULL
);
GO

-- 9. Chèn dữ liệu danh mục ban đầu
INSERT INTO VaiTro (TenVaiTro) VALUES 
(N'Admin'), 
(N'Ban Điều hành'), 
(N'Thành viên');

INSERT INTO BanChuyenMon (TenBan) VALUES 
(N'Ban Đối ngoại'), 
(N'Ban Sự kiện'), 
(N'Ban Truyền thông');

-- 10. Chèn Tài khoản mẫu đầy đủ 3 cấp độ (Mật khẩu mặc định đều là 123456)
INSERT INTO NhanSu (MaSV, MatKhau, HoTen, Lop, SoDienThoai, TrangThai, ID_VaiTro, ID_Ban) VALUES 
('ADMIN01', '123456', N'Quản trị viên Văn phòng Đoàn', N'Văn phòng Đoàn', '0912345678', 1, 1, NULL),
('BDH01',   '123456', N'Nguyễn Văn A (Trưởng Ban ĐN)', N'D19CNPM1',       '0987654321', 1, 2, 1),
('TV01',    '123456', N'Trần Thị B (Thành viên)',      N'D19CNPM2',       '0933445566', 1, 3, 1),
('TV02',    '123456', N'Lê Văn C (Thành viên)',        N'D20QTKD1',       '0977889900', 1, 3, 2);

-- 11. Cấp quyền tác nghiệp mẫu cho tài khoản Ban Điều Hành BDH01
INSERT INTO PhanQuyenBanDieuHanh (TenDangNhap, QuyenDoiNgoai, QuyenSuKien, QuyenTruyenThong, QuyenXuatBaoCao) VALUES
('BDH01', 1, 1, 0, 1);
GO

-- 12. Kiểm tra danh sách tài khoản vừa chèn
SELECT 
    ns.MaSV AS [Mã SV / Tên ĐN], 
    ns.HoTen AS [Họ và Tên], 
    ns.Lop AS [Lớp], 
    vt.TenVaiTro AS [Vai Trò],
    ISNULL(bcm.TenBan, N'Không thuộc ban') AS [Ban Chuyên Môn],
    CASE WHEN ns.TrangThai = 1 THEN N'Hoạt động' ELSE N'Tạm khóa' END AS [Trạng Thái]
FROM NhanSu ns
JOIN VaiTro vt ON ns.ID_VaiTro = vt.ID_VaiTro
LEFT JOIN BanChuyenMon bcm ON ns.ID_Ban = bcm.ID_Ban;
-- 1. Bổ sung các cột còn thiếu cho bảng CaTruc (nếu chưa có)
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'CaTruc' AND COLUMN_NAME = 'TrangThai')
BEGIN
    ALTER TABLE CaTruc ADD Thu NVARCHAR(20), MaSV VARCHAR(20), HoTen NVARCHAR(100), TrangThai NVARCHAR(50) DEFAULT N'Mở đăng ký', GhiChu NVARCHAR(200);
END
GO

-- 2. Thêm dữ liệu Ca trực đang "Mở đăng ký"
INSERT INTO CaTruc (BuoiTruc, NgayTruc, Thu, TrangThai, GhiChu) VALUES
(N'Sáng',  '2026-11-02', N'Thứ 2', N'Mở đăng ký', N'Trực bàn đón tiếp sinh viên'),
(N'Chiều', '2026-11-02', N'Thứ 2', N'Mở đăng ký', N'Trực văn phòng Đoàn'),
(N'Sáng',  '2026-11-03', N'Thứ 3', N'Mở đăng ký', N'Hỗ trợ tiếp nhận hồ sơ'),
(N'Chiều', '2026-11-03', N'Thứ 3', N'Mở đăng ký', N'Trực tổng hợp văn bản');

-- 3. Thêm dữ liệu Sự kiện đang diễn ra / sắp diễn ra
INSERT INTO SuKien (TenSuKien, NgayToChuc, DiaDiem, KinhPhi, TrangThai) VALUES
(N'Chào Tân Sinh Viên Khóa D19', '2026-11-05', N'Hội trường A1', 15000000, N'Đang diễn ra'),
(N'Giải Bóng Đá Cúp Thanh Niên EPU', '2026-11-12', N'Sân bóng EPU', 8500000, N'Sắp diễn ra'),
(N'Hiến Máu Nhân Đạo Giọt Hồng', '2026-11-20', N'Sảnh nhà B', 5000000, N'Sắp diễn ra');

-- 4. Thêm Nhiệm vụ Truyền thông giao riêng cho tài khoản TV01
INSERT INTO NhiemVuTruyenThong (TenNhiemVu, MaSV_DuocGiao, HanChot, TrangThai) VALUES
(N'Viết bài Fanpage giới thiệu Sự kiện Chào Tân Sinh Viên', 'TV01', '2026-11-03', N'Đang thực hiện'),
(N'Thiết kế Banner Giải Bóng Đá Cúp Thanh Niên', 'TV01', '2026-11-10', N'Chờ duyệt');
GO