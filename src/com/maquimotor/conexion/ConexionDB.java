package com.maquimotor.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=maquimotor_db;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "Admin123*";

    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("[OK] Conectado exitosamente a SQL Server.");
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión a la Base de Datos: " + e.getMessage());
        }
        return con;
    }
}