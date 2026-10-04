package com.maquimotor.conexion;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InicializadorDB {

    public static void inicializarTablas() {
        String[] sentencias = new String[] {
            // Eliminar tablas previas en orden de dependencia
            "IF OBJECT_ID('DetalleCotizacion', 'U') IS NOT NULL DROP TABLE DetalleCotizacion;",
            "IF OBJECT_ID('Cotizacion', 'U') IS NOT NULL DROP TABLE Cotizacion;",
            "IF OBJECT_ID('Equipo', 'U') IS NOT NULL DROP TABLE Equipo;",
            "IF OBJECT_ID('FichaTecnica', 'U') IS NOT NULL DROP TABLE FichaTecnica;",
            "IF OBJECT_ID('Cliente', 'U') IS NOT NULL DROP TABLE Cliente;",
            "IF OBJECT_ID('Usuario', 'U') IS NOT NULL DROP TABLE Usuario;",

            // 1. Tabla Usuario (RF-01)
            "CREATE TABLE Usuario (" +
            "    idUsuario INT IDENTITY(1,1) PRIMARY KEY," +
            "    nombreUsuario VARCHAR(50) NOT NULL UNIQUE," +
            "    password VARCHAR(100) NOT NULL," +
            "    rol VARCHAR(30) NOT NULL" +
            ");",

            // 2. Tabla Cliente (RF-05)
            "CREATE TABLE Cliente (" +
            "    idCliente INT PRIMARY KEY," +
            "    razonSocial VARCHAR(100) NOT NULL," +
            "    documento VARCHAR(15) NOT NULL," +
            "    contacto VARCHAR(100)," +
            "    direccionDespacho VARCHAR(150)" +
            ");",

            // 3. Tabla FichaTecnica
            "CREATE TABLE FichaTecnica (" +
            "    idFicha INT IDENTITY(1,1) PRIMARY KEY," +
            "    potenciaHP DECIMAL(10,2) NOT NULL," +
            "    cilindrada DECIMAL(10,2) NOT NULL," +
            "    combustible VARCHAR(30) NOT NULL," +
            "    compatibilidad VARCHAR(150)" +
            ");",

            // 4. Tabla Equipo (RF-02, RF-03, RF-04)
            "CREATE TABLE Equipo (" +
            "    idEquipo INT PRIMARY KEY," +
            "    codigo VARCHAR(30) NOT NULL UNIQUE," +
            "    numeroSerie VARCHAR(50) NOT NULL UNIQUE," +
            "    marca VARCHAR(50) NOT NULL," +
            "    modelo VARCHAR(50) NOT NULL," +
            "    aplicacion VARCHAR(30) NOT NULL," +
            "    precioBase DECIMAL(10,2) NOT NULL," +
            "    stockDisponible INT NOT NULL," +
            "    stockMinimo INT NOT NULL," +
            "    estado VARCHAR(30) DEFAULT 'Disponible'," +
            "    idFicha INT FOREIGN KEY REFERENCES FichaTecnica(idFicha)" +
            ");",

            // 5. Tabla Cotizacion (RF-06 a RF-09 con fechaVencimiento para reservas 48h)
            "CREATE TABLE Cotizacion (" +
            "    idCotizacion INT PRIMARY KEY," +
            "    idCliente INT FOREIGN KEY REFERENCES Cliente(idCliente)," +
            "    idUsuario INT FOREIGN KEY REFERENCES Usuario(idUsuario)," +
            "    fechaEmision DATETIME DEFAULT GETDATE()," +
            "    fechaVencimiento DATETIME NULL," +
            "    estado VARCHAR(30) DEFAULT 'Vigente'," +
            "    subtotal DECIMAL(10,2) NOT NULL," +
            "    igv DECIMAL(10,2) NOT NULL," +
            "    total DECIMAL(10,2) NOT NULL" +
            ");",

            // 6. Tabla DetalleCotizacion
            "CREATE TABLE DetalleCotizacion (" +
            "    idDetalle INT IDENTITY(1,1) PRIMARY KEY," +
            "    idCotizacion INT FOREIGN KEY REFERENCES Cotizacion(idCotizacion)," +
            "    idEquipo INT FOREIGN KEY REFERENCES Equipo(idEquipo)," +
            "    cantidad INT NOT NULL," +
            "    precioUnitario DECIMAL(10,2) NOT NULL," +
            "    subtotalItem DECIMAL(10,2) NOT NULL" +
            ");",

            // Inserción de Usuarios de prueba
            "INSERT INTO Usuario (nombreUsuario, password, rol) VALUES ('admin', 'admin123', 'Administrador');",
            "INSERT INTO Usuario (nombreUsuario, password, rol) VALUES ('asesor1', '123456', 'Asesor Comercial');",
            "INSERT INTO Usuario (nombreUsuario, password, rol) VALUES ('almacen1', '123456', 'Jefe de Almacén');",

            // Inserción de Fichas Técnicas
            "INSERT INTO FichaTecnica (potenciaHP, cilindrada, combustible, compatibilidad) VALUES (250.0, 6.7, 'Diésel', 'Excavadoras y grupos electrógenos');",
            "INSERT INTO FichaTecnica (potenciaHP, cilindrada, combustible, compatibilidad) VALUES (15.0, 1.2, 'Diésel', 'Bombas de alta presión');",

            // Inserción de Equipos de prueba
            "INSERT INTO Equipo (idEquipo, codigo, numeroSerie, marca, modelo, aplicacion, precioBase, stockDisponible, stockMinimo, estado, idFicha) " +
            "VALUES (1, 'EQ-001', 'SN-CUM-9982', 'Cummins', 'QSB6.7', 'Minería', 18500.00, 2, 3, 'Disponible', 1);",
            "INSERT INTO Equipo (idEquipo, codigo, numeroSerie, marca, modelo, aplicacion, precioBase, stockDisponible, stockMinimo, estado, idFicha) " +
            "VALUES (2, 'EQ-002', 'SN-YAN-4411', 'Yanmar', '3TNV88', 'Agricultura', 4200.00, 8, 2, 'Disponible', 2);",

            // Inserción de Cliente de prueba
            "INSERT INTO Cliente (idCliente, razonSocial, documento, contacto, direccionDespacho) " +
            "VALUES (1, 'Minera Los Andes S.A.C.', '20551234567', 'Ing. Carlos Ramos', 'Av. Minerales 104, Callao');"
        };

        Connection con = ConexionDB.conectar();
        if (con == null) {
            System.err.println("No se pudo conectar para inicializar la BD.");
            return;
        }

        try (Connection c = con; Statement stmt = c.createStatement()) {
            for (String sql : sentencias) {
                stmt.execute(sql);
            }
            System.out.println("✅ Base de datos inicializada correctamente con tablas y datos de prueba.");
        } catch (SQLException e) {
            System.err.println("❌ Error al inicializar tablas: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        inicializarTablas();
    }
}