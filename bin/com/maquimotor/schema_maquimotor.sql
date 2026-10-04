-- ============================================================================
-- PROYECTO: Sistema de Gestión de Inventario y Cotizaciones
-- EMPRESA: MaquiMotor Perú S.A.C.
-- MOTOR: Microsoft SQL Server
-- BASE DE DATOS: maquimotor_db
-- ============================================================================

-- 1. Creación de la Base de Datos
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'maquimotor_db')
BEGIN
    CREATE DATABASE maquimotor_db;
END
GO

USE maquimotor_db;
GO

-- 2. Limpieza de tablas previas (en orden inverso de dependencias FK)
IF OBJECT_ID('dbo.DetalleCotizacion', 'U') IS NOT NULL DROP TABLE dbo.DetalleCotizacion;
IF OBJECT_ID('dbo.Cotizacion', 'U') IS NOT NULL DROP TABLE dbo.Cotizacion;
IF OBJECT_ID('dbo.Equipo', 'U') IS NOT NULL DROP TABLE dbo.Equipo;
IF OBJECT_ID('dbo.FichaTecnica', 'U') IS NOT NULL DROP TABLE dbo.FichaTecnica;
IF OBJECT_ID('dbo.Cliente', 'U') IS NOT NULL DROP TABLE dbo.Cliente;
IF OBJECT_ID('dbo.Usuario', 'U') IS NOT NULL DROP TABLE dbo.Usuario;
GO

-- 3. Tabla: dbo.Usuario (RF-01)
CREATE TABLE dbo.Usuario (
    idUsuario INT IDENTITY(1,1) NOT NULL,
    nombreUsuario VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    CONSTRAINT PK_Usuario PRIMARY KEY CLUSTERED (idUsuario),
    CONSTRAINT UQ_Usuario_nombreUsuario UNIQUE NONCLUSTERED (nombreUsuario)
);
GO

-- 4. Tabla: dbo.Cliente (RF-05, RF-06, RF-07)
CREATE TABLE dbo.Cliente (
    idCliente INT NOT NULL,
    razonSocial VARCHAR(100) NOT NULL,
    documento VARCHAR(15) NOT NULL,
    contacto VARCHAR(100) NULL,
    direccionDespacho VARCHAR(150) NULL,
    CONSTRAINT PK_Cliente PRIMARY KEY CLUSTERED (idCliente)
);
GO

-- 5. Tabla: dbo.FichaTecnica (RF-04)
CREATE TABLE dbo.FichaTecnica (
    idFicha INT IDENTITY(1,1) NOT NULL,
    potenciaHP DECIMAL(10,2) NOT NULL,
    cilindrada DECIMAL(10,2) NOT NULL,
    combustible VARCHAR(30) NOT NULL,
    compatibilidad VARCHAR(150) NULL,
    CONSTRAINT PK_FichaTecnica PRIMARY KEY CLUSTERED (idFicha)
);
GO

-- 6. Tabla: dbo.Equipo (RF-02, RF-03, RF-04)
CREATE TABLE dbo.Equipo (
    idEquipo INT NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    numeroSerie VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    aplicacion VARCHAR(30) NOT NULL,
    precioBase DECIMAL(10,2) NOT NULL,
    stockDisponible INT NOT NULL,
    stockMinimo INT NOT NULL,
    estado VARCHAR(30) NULL CONSTRAINT DF_Equipo_estado DEFAULT ('Disponible'),
    idFicha INT NULL,
    CONSTRAINT PK_Equipo PRIMARY KEY CLUSTERED (idEquipo),
    CONSTRAINT UQ_Equipo_codigo UNIQUE NONCLUSTERED (codigo),
    CONSTRAINT UQ_Equipo_numeroSerie UNIQUE NONCLUSTERED (numeroSerie),
    CONSTRAINT FK_Equipo_idFicha FOREIGN KEY (idFicha) REFERENCES dbo.FichaTecnica(idFicha)
);
GO

-- 7. Tabla: dbo.Cotizacion (RF-06 a RF-09)
CREATE TABLE dbo.Cotizacion (
    idCotizacion INT NOT NULL,
    idCliente INT NULL,
    idUsuario INT NULL,
    fechaEmision DATETIME NULL CONSTRAINT DF_Cotizacion_fechaEmision DEFAULT (GETDATE()),
    fechaVencimiento DATETIME NULL,
    estado VARCHAR(30) NULL CONSTRAINT DF_Cotizacion_estado DEFAULT ('Vigente'),
    subtotal DECIMAL(10,2) NOT NULL,
    igv DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    CONSTRAINT PK_Cotizacion PRIMARY KEY CLUSTERED (idCotizacion),
    CONSTRAINT FK_Cotizacion_idCliente FOREIGN KEY (idCliente) REFERENCES dbo.Cliente(idCliente),
    CONSTRAINT FK_Cotizacion_idUsuario FOREIGN KEY (idUsuario) REFERENCES dbo.Usuario(idUsuario)
);
GO

-- 8. Tabla: dbo.DetalleCotizacion (Composición mercantil con Cotización)
CREATE TABLE dbo.DetalleCotizacion (
    idDetalle INT IDENTITY(1,1) NOT NULL,
    idCotizacion INT NULL,
    idEquipo INT NULL,
    cantidad INT NOT NULL,
    precioUnitario DECIMAL(10,2) NOT NULL,
    subtotalItem DECIMAL(10,2) NOT NULL,
    CONSTRAINT PK_DetalleCotizacion PRIMARY KEY CLUSTERED (idDetalle),
    CONSTRAINT FK_DetalleCotizacion_idCotizacion FOREIGN KEY (idCotizacion) REFERENCES dbo.Cotizacion(idCotizacion),
    CONSTRAINT FK_DetalleCotizacion_idEquipo FOREIGN KEY (idEquipo) REFERENCES dbo.Equipo(idEquipo)
);
GO

-- ============================================================================
-- 9. Población Inicial de Datos Semilla (DML)
-- ============================================================================

-- Usuarios del sistema
INSERT INTO dbo.Usuario (nombreUsuario, password, rol) VALUES 
('admin', 'admin123', 'Administrador'),
('asesor1', '123456', 'Asesor Comercial'),
('almacen1', '123456', 'Jefe de Almacén');

-- Clientes corporativos
INSERT INTO dbo.Cliente (idCliente, razonSocial, documento, contacto, direccionDespacho) VALUES 
(1, 'Minera Los Andes S.A.C.', '20551234567', 'Ing. Carlos Ramos', 'Av. Minerales 104, Callao'),
(2, 'Agropecuaria del Norte S.A.C.', '20608912341', 'Lic. Mariana Flores', 'Panamericana Norte Km 520, Piura'),
(3, 'Constructora San Martín S.R.L.', '20489912039', 'Arq. Roberto Vega', 'Av. Nicolás Ayllón 850, Ate, Lima');

-- Fichas Técnicas de maquinaria
INSERT INTO dbo.FichaTecnica (potenciaHP, cilindrada, combustible, compatibilidad) VALUES 
(250.00, 6.70, 'Diésel', 'Excavadoras y grupos electrógenos'),
(15.00, 1.20, 'Diésel', 'Bombas de alta presión e irrigación'),
(450.00, 15.00, 'Diésel', 'Camiones mineros y generadores pesados'),
(85.00, 3.30, 'Diésel B5', 'Tractores agrícolas e industriales');

-- Maquinaria pesada y motores industriales
INSERT INTO dbo.Equipo (idEquipo, codigo, numeroSerie, marca, modelo, aplicacion, precioBase, stockDisponible, stockMinimo, estado, idFicha) VALUES 
(1, 'EQ-001', 'SN-CUM-9982', 'Cummins', 'QSB6.7', 'Minería', 18500.00, 2, 3, 'Disponible', 1), -- Stock crítico (2 <= 3)
(2, 'EQ-002', 'SN-YAN-4411', 'Yanmar', '3TNV88', 'Agricultura', 4200.00, 8, 2, 'Disponible', 2),
(3, 'EQ-003', 'SN-CAT-7731', 'Caterpillar', 'C15 ACERT', 'Minería', 45000.00, 3, 1, 'Disponible', 3),
(4, 'EQ-004', 'SN-PRK-2109', 'Perkins', '1104D-44T', 'Construcción', 9800.00, 5, 2, 'Disponible', 4);

-- Cotización de prueba demostrativa (con reserva temporal de 48 horas)
INSERT INTO dbo.Cotizacion (idCotizacion, idCliente, idUsuario, fechaEmision, fechaVencimiento, estado, subtotal, igv, total) VALUES 
(101, 1, 2, GETDATE(), DATEADD(HOUR, 48, GETDATE()), 'Vigente', 18500.00, 3330.00, 21830.00);

-- Detalle de la cotización 101
INSERT INTO dbo.DetalleCotizacion (idCotizacion, idEquipo, cantidad, precioUnitario, subtotalItem) VALUES 
(101, 1, 1, 18500.00, 18500.00);
GO