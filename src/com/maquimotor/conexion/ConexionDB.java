package com.maquimotor.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    private static final String HOST = "localhost:1433";
    private static final String DB_NAME = "maquimotor_db";
    private static final String USER = "sa";
    private static final String PASS = "Admin123*";

    // URL para conectarse a la base de datos del proyecto
    private static final String URL = "jdbc:sqlserver://" + HOST + ";databaseName=" + DB_NAME + ";encrypt=true;trustServerCertificate=true;";
    
    // URL maestra para crear la BD si no existe aún
    private static final String URL_MASTER = "jdbc:sqlserver://" + HOST + ";databaseName=master;encrypt=true;trustServerCertificate=true;";

    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión a la Base de Datos: " + e.getMessage());
        }
        return con;
    }

    // Se ejecuta al inicio: crea la BD si no existe y verifica el esquema
    public static void inicializarEsquemaAutomatico() {
        // 1. Asegurar que la base de datos exista conectándose a 'master'
        String sqlCrearDB = 
            "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'" + DB_NAME + "') " +
            "BEGIN " +
            "   CREATE DATABASE [" + DB_NAME + "]; " +
            "END";

        try (Connection conMaster = DriverManager.getConnection(URL_MASTER, USER, PASS);
             Statement stmtMaster = conMaster.createStatement()) {
            stmtMaster.execute(sqlCrearDB);
            System.out.println("✅ [BD] Base de datos '" + DB_NAME + "' verificada/creada.");
        } catch (SQLException e) {
            System.err.println("⚠️ [BD] Error al verificar/crear base de datos en master: " + e.getMessage());
        }

        // 2. Conectarse a la BD maquimotor_db y verificar la columna fechaVencimiento
        Connection con = conectar();
        if (con == null) {
            System.err.println("⚠️ [BD] No se pudo conectar a " + DB_NAME + " para verificar columnas. Verifique usuario/clave en SQL Server.");
            return;
        }

        String sqlColumna = 
            "IF OBJECT_ID('Cotizacion', 'U') IS NOT NULL " +
            "BEGIN " +
            "   IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Cotizacion') AND name = 'fechaVencimiento') " +
            "   BEGIN " +
            "       ALTER TABLE Cotizacion ADD fechaVencimiento DATETIME NULL; " +
            "       EXEC('UPDATE Cotizacion SET fechaVencimiento = DATEADD(hour, 48, fechaEmision) WHERE fechaVencimiento IS NULL;'); " +
            "   END " +
            "END";

        try (Statement stmt = con.createStatement()) {
            stmt.execute(sqlColumna);
            System.out.println("✅ [BD] Esquema verificado: soporte para reservas de 48h activo.");
        } catch (SQLException e) {
            System.err.println("⚠️ [BD] Nota al verificar esquema: " + e.getMessage());
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }
    }
}