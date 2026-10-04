package com.maquimotor.app;

import com.maquimotor.conexion.ConexionDB;
import com.maquimotor.conexion.InicializadorDB;
import com.maquimotor.vista.LoginView;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // 1. Asegurar creación de base de datos
        ConexionDB.inicializarEsquemaAutomatico();

        // 2. Verificar si existen las tablas; si faltan, poblarlas automáticamente
        try (Connection con = ConexionDB.conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM sys.tables WHERE name = 'Usuario'")) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("ℹ️ Tablas no detectadas. Inicializando estructura y datos de prueba...");
                InicializadorDB.inicializarTablas();
            }
        } catch (Exception e) {
            System.err.println("Error verificando tablas: " + e.getMessage());
        }

        // 3. Abrir la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}