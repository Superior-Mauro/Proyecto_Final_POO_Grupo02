package com.maquimotor.dao;

import com.maquimotor.conexion.ConexionDB;
import com.maquimotor.modelo.Equipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public boolean insertar(Equipo equipo) {
        String sql = "INSERT INTO Equipo (codigo, numeroSerie, marca, modelo, stock, precioUnitario) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, equipo.getCodigo());
            pstmt.setString(2, equipo.getNumeroSerie());
            pstmt.setString(3, equipo.getMarca());
            pstmt.setString(4, equipo.getModelo());
            pstmt.setInt(5, equipo.getStock());
            pstmt.setDouble(6, equipo.getPrecioUnitario());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar el equipo en la BD: " + e.getMessage());
            return false;
        }
    }

    public Equipo buscar(int idEquipo) {
        String sql = "SELECT * FROM Equipo WHERE idEquipo = ?";
        Equipo equipo = null;
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEquipo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    equipo = new Equipo(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getInt(6),
                        rs.getDouble(7)
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar equipo: " + e.getMessage());
        }
        return equipo;
    }

    public List<Equipo> listar() {
        String sql = "SELECT * FROM Equipo";
        List<Equipo> lista = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Equipo equipo = new Equipo(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getInt(6),
                    rs.getDouble(7)
                );
                lista.add(equipo);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Equipo equipo) {
        String sql = "UPDATE Equipo SET codigo = ?, numeroSerie = ?, marca = ?, modelo = ?, stock = ?, precioUnitario = ? WHERE idEquipo = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, equipo.getCodigo());
            pstmt.setString(2, equipo.getNumeroSerie());
            pstmt.setString(3, equipo.getMarca());
            pstmt.setString(4, equipo.getModelo());
            pstmt.setInt(5, equipo.getStock());
            pstmt.setDouble(6, equipo.getPrecioUnitario());
            pstmt.setInt(7, equipo.getIdEquipo());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar equipo: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idEquipo) {
        String sql = "DELETE FROM Equipo WHERE idEquipo = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setInt(1, idEquipo);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar equipo: " + e.getMessage());
            return false;
        }
    }
}