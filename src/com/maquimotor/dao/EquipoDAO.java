package com.maquimotor.dao;

import com.maquimotor.conexion.ConexionDB;
import com.maquimotor.modelo.Equipo;
import com.maquimotor.modelo.FichaTecnica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public boolean insertar(Equipo equipo) {
        String sqlFicha = "INSERT INTO FichaTecnica (potenciaHP, cilindrada, combustible, compatibilidad) VALUES (?, ?, ?, ?)";
        String sqlEquipo = "INSERT INTO Equipo (idEquipo, codigo, numeroSerie, marca, modelo, aplicacion, precioBase, stockDisponible, stockMinimo, estado, idFicha) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            con.setAutoCommit(false);

            int idFichaGenerada = 0;
            try (PreparedStatement pstF = con.prepareStatement(sqlFicha, Statement.RETURN_GENERATED_KEYS)) {
                pstF.setDouble(1, equipo.getFichaTecnica().getPotenciaHP());
                pstF.setDouble(2, equipo.getFichaTecnica().getCilindrada());
                pstF.setString(3, equipo.getFichaTecnica().getCombustible());
                pstF.setString(4, equipo.getFichaTecnica().getCompatibilidad());
                pstF.executeUpdate();

                try (ResultSet rs = pstF.getGeneratedKeys()) {
                    if (rs.next()) idFichaGenerada = rs.getInt(1);
                }
            }

            try (PreparedStatement pstE = con.prepareStatement(sqlEquipo)) {
                pstE.setInt(1, equipo.getIdEquipo());
                pstE.setString(2, equipo.getCodigo());
                pstE.setString(3, equipo.getNumeroSerie());
                pstE.setString(4, equipo.getMarca());
                pstE.setString(5, equipo.getModelo());
                pstE.setString(6, equipo.getAplicacion());
                pstE.setDouble(7, equipo.getPrecioBase());
                pstE.setInt(8, equipo.getStockDisponible());
                pstE.setInt(9, equipo.getStockMinimo());
                pstE.setString(10, equipo.getEstado());
                pstE.setInt(11, idFichaGenerada);
                pstE.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar equipo: " + e.getMessage());
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
    // Método para actualizar datos del equipo y estado físico/operativo (RF-02 y RF-03)
    public boolean actualizar(Equipo equipo) {
        String sql = "UPDATE Equipo SET codigo = ?, numeroSerie = ?, marca = ?, modelo = ?, " +
                     "aplicacion = ?, precioBase = ?, stockDisponible = ?, stockMinimo = ?, estado = ? " +
                     "WHERE idEquipo = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, equipo.getCodigo());
            pstmt.setString(2, equipo.getNumeroSerie());
            pstmt.setString(3, equipo.getMarca());
            pstmt.setString(4, equipo.getModelo());
            pstmt.setString(5, equipo.getAplicacion());
            pstmt.setDouble(6, equipo.getPrecioBase());
            pstmt.setInt(7, equipo.getStockDisponible());
            pstmt.setInt(8, equipo.getStockMinimo());
            pstmt.setString(9, equipo.getEstado());
            pstmt.setInt(10, equipo.getIdEquipo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar equipo: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar equipo (con manejo de integridad referencial)
    public boolean eliminar(int idEquipo) {
        String sql = "DELETE FROM Equipo WHERE idEquipo = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, idEquipo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar equipo: " + e.getMessage());
            return false;
        }
    }

    public List<Equipo> listar() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT e.idEquipo, e.codigo, e.numeroSerie, e.marca, e.modelo, e.aplicacion, " +
                     "e.precioBase, e.stockDisponible, e.stockMinimo, e.estado, " +
                     "f.idFicha, f.potenciaHP, f.cilindrada, f.combustible, f.compatibilidad " +
                     "FROM Equipo e LEFT JOIN FichaTecnica f ON e.idFicha = f.idFicha";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                FichaTecnica f = new FichaTecnica(
                    rs.getDouble("potenciaHP"),
                    rs.getDouble("cilindrada"),
                    rs.getString("combustible"),
                    rs.getString("compatibilidad")
                );
                f.setIdFicha(rs.getInt("idFicha"));

                Equipo eq = new Equipo(
                    rs.getInt("idEquipo"),
                    rs.getString("codigo"),
                    rs.getString("numeroSerie"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getString("aplicacion"),
                    rs.getDouble("precioBase"),
                    rs.getInt("stockDisponible"),
                    rs.getInt("stockMinimo"),
                    rs.getString("estado"),
                    f
                );
                lista.add(eq);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos: " + e.getMessage());
        }
        return lista;
    }
}