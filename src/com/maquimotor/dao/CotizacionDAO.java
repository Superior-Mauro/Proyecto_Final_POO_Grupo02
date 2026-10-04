package com.maquimotor.dao;

import com.maquimotor.conexion.ConexionDB;
import com.maquimotor.modelo.Cotizacion;
import com.maquimotor.modelo.DetalleCotizacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CotizacionDAO {

    public boolean registrarCotizacionCompleta(Cotizacion cot, List<DetalleCotizacion> detalles) {
        String sqlCot = "INSERT INTO Cotizacion (idCotizacion, idCliente, idUsuario, subtotal, igv, total, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlDet = "INSERT INTO DetalleCotizacion (idCotizacion, idEquipo, cantidad, precioUnitario, subtotalItem) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE Equipo SET stockDisponible = stockDisponible - ?, " +
                          "estado = CASE WHEN stockDisponible - ? <= 0 THEN 'Reservado' ELSE estado END " +
                          "WHERE idEquipo = ? AND stockDisponible >= ?";

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            con.setAutoCommit(false);

            try (PreparedStatement pstCot = con.prepareStatement(sqlCot)) {
                pstCot.setInt(1, cot.getIdCotizacion());
                pstCot.setInt(2, cot.getIdCliente());
                pstCot.setInt(3, cot.getIdUsuario() > 0 ? cot.getIdUsuario() : 1);
                pstCot.setDouble(4, cot.getSubtotal());
                pstCot.setDouble(5, cot.getIgv());
                pstCot.setDouble(6, cot.getTotal());
                pstCot.setString(7, "Vigente");
                pstCot.executeUpdate();
            }

            try (PreparedStatement pstDet = con.prepareStatement(sqlDet);
                 PreparedStatement pstStk = con.prepareStatement(sqlStock)) {

                for (DetalleCotizacion d : detalles) {
                    pstDet.setInt(1, cot.getIdCotizacion());
                    pstDet.setInt(2, d.getEquipo().getIdEquipo());
                    pstDet.setInt(3, d.getCantidad());
                    pstDet.setDouble(4, d.getPrecioUnitario());
                    pstDet.setDouble(5, d.getSubtotalItem());
                    pstDet.executeUpdate();

                    pstStk.setInt(1, d.getCantidad());
                    pstStk.setInt(2, d.getCantidad());
                    pstStk.setInt(3, d.getEquipo().getIdEquipo());
                    pstStk.setInt(4, d.getCantidad());
                    int rows = pstStk.executeUpdate();

                    if (rows == 0) {
                        con.rollback();
                        return false;
                    }
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error en transacción de cotización: " + e.getMessage());
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public List<Cotizacion> listar() {
        String sql = "SELECT idCotizacion, idCliente, idUsuario, subtotal, igv, total, estado FROM Cotizacion";
        List<Cotizacion> lista = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Cotizacion c = new Cotizacion();
                c.setIdCotizacion(rs.getInt("idCotizacion"));
                c.setIdCliente(rs.getInt("idCliente"));
                c.setIdUsuario(rs.getInt("idUsuario"));
                c.setSubtotal(rs.getDouble("subtotal"));
                c.setIgv(rs.getDouble("igv"));
                c.setTotal(rs.getDouble("total"));
                c.setEstado(rs.getString("estado"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar cotizaciones: " + e.getMessage());
        }
        return lista;
    }
}