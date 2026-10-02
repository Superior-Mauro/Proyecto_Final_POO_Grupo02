package com.maquimotor.dao;

import com.maquimotor.conexion.ConexionDB;
import com.maquimotor.modelo.Cotizacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CotizacionDAO {

    // 1. INSERTAR
    public boolean insertar(Cotizacion cotizacion) {
        String sql = "INSERT INTO Cotizacion (idCotizacion, idCliente, subtotal, igv, total) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = ConexionDB.conectar()) {
            try (Statement stmt = con.createStatement()) {
                stmt.execute("SET IDENTITY_INSERT Cotizacion ON");
            } catch (SQLException e) {
                
            }

            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, cotizacion.getIdCotizacion());
                pstmt.setInt(2, cotizacion.getIdCliente());
                pstmt.setDouble(3, cotizacion.getSubtotal());
                pstmt.setDouble(4, cotizacion.getIgv());
                pstmt.setDouble(5, cotizacion.getTotal());

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. BUSCAR por ID
    public Cotizacion buscar(int idCotizacion) {
        String sql = "SELECT idCotizacion, idCliente, subtotal, igv, total FROM Cotizacion WHERE idCotizacion = ?";
        Cotizacion cotizacion = null;

        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCotizacion);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cotizacion = new Cotizacion();
                    cotizacion.setIdCotizacion(rs.getInt("idCotizacion"));
                    cotizacion.setIdCliente(rs.getInt("idCliente"));
                    cotizacion.setSubtotal(rs.getDouble("subtotal"));
                    cotizacion.setIgv(rs.getDouble("igv"));
                    cotizacion.setTotal(rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cotizacion;
    }

    // 3. LISTAR todos
    public List<Cotizacion> listar() {
        String sql = "SELECT idCotizacion, idCliente, subtotal, igv, total FROM Cotizacion";
        List<Cotizacion> lista = new ArrayList<>();

        try (Connection con = ConexionDB.conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Cotizacion cotizacion = new Cotizacion();
                cotizacion.setIdCotizacion(rs.getInt("idCotizacion"));
                cotizacion.setIdCliente(rs.getInt("idCliente"));
                cotizacion.setSubtotal(rs.getDouble("subtotal"));
                cotizacion.setIgv(rs.getDouble("igv"));
                cotizacion.setTotal(rs.getDouble("total"));
                lista.add(cotizacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 4. ACTUALIZAR
    public boolean actualizar(Cotizacion cotizacion) {
        String sql = "UPDATE Cotizacion SET idCliente = ?, subtotal = ?, igv = ?, total = ? WHERE idCotizacion = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, cotizacion.getIdCliente());
            pstmt.setDouble(2, cotizacion.getSubtotal());
            pstmt.setDouble(3, cotizacion.getIgv());
            pstmt.setDouble(4, cotizacion.getTotal());
            pstmt.setInt(5, cotizacion.getIdCotizacion());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. ELIMINAR
    public boolean eliminar(int idCotizacion) {
        String sql = "DELETE FROM Cotizacion WHERE idCotizacion = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCotizacion);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}