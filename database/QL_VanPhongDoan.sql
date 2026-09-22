-- 1. Tạo Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'QL_VanPhongDoan')
BEGIN
    CREATE DATABASE QL_VanPhongDoan;
END
GO

USE QL_VanPhongDoan;
GO

-- 2. Xóa bảng cũ nếu đã tồn tại (theo thứ tự quan hệ khóa ngoại để tránh lỗi)
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
    MatKhau VARCHAR(255) NOT NULL,
    HoTen NVARCHAR(100) NOT NULL,
    Lop NVARCHAR(50),
    SoDienThoai VARCHAR(15),
    TrangThai BIT NOT NULL DEFAULT 1, -- 1: Hoạt động, 0: Khóa
    ID_VaiTro INT NOT NULL,
    ID_Ban INT NULL,
    CONSTRAINT FK_NhanSu_VaiTro FOREIGN KEY (ID_VaiTro) REFERENCES VaiTro(ID_VaiTro),
    CONSTRAINT FK_NhanSu_BanChuyenMon FOREIGN KEY (ID_Ban) REFERENCES BanChuyenMon(ID_Ban)
);
GO

-- 5. Tạo bảng Quản lý ca trực
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

-- 6. Tạo bảng Quản lý sự kiện
CREATE TABLE SuKien (
    ID_SuKien INT IDENTITY(1,1) PRIMARY KEY,
    TenSuKien NVARCHAR(200) NOT NULL,
    NgayToChuc DATE NOT NULL,
    DiaDiem NVARCHAR(200),
    TrangThai NVARCHAR(50) NOT NULL DEFAULT N'Sắp diễn ra'
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

-- 7. Tạo bảng Quản lý nhiệm vụ truyền thông
CREATE TABLE NhiemVuTruyenThong (
    ID_NhiemVu INT IDENTITY(1,1) PRIMARY KEY,
    TenNhiemVu NVARCHAR(200) NOT NULL,
    MaSV_DuocGiao VARCHAR(20) NULL,
    HanChot DATE NULL,
    TrangThai NVARCHAR(50) NOT NULL DEFAULT N'Đang thực hiện',
    CONSTRAINT FK_NhiemVu_NhanSu FOREIGN KEY (MaSV_DuocGiao) REFERENCES NhanSu(MaSV) ON DELETE SET NULL
);
GO

-- 8. Chèn dữ liệu mẫu ban đầu để kiểm thử
INSERT INTO VaiTro (TenVaiTro) VALUES 
(N'Admin'), 
(N'Ban Điều hành'), 
(N'Thành viên');

INSERT INTO BanChuyenMon (TenBan) VALUES 
(N'Ban Đối ngoại'), 
(N'Ban Sự kiện'), 
(N'Ban Truyền thông');

-- Thêm 1 tài khoản Admin mặc định (mật khẩu: 123456)
INSERT INTO NhanSu (MaSV, MatKhau, HoTen, Lop, SoDienThoai, TrangThai, ID_VaiTro, ID_Ban)
VALUES ('ADMIN01', '123456', N'Quản trị viên Văn phòng Đoàn', N'Văn phòng Đoàn', '0123456789', 1, 1, NULL);
GO
