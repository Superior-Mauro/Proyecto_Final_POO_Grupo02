package com.maquimotor.dao;

import com.maquimotor.conexion.ConexionDB;
import com.maquimotor.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO Cliente (idCliente, razonSocial, documento, contacto, direccionDespacho) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, cliente.getIdCliente());
            pstmt.setString(2, cliente.getRazonSocial());
            pstmt.setString(3, cliente.getDocumento());
            pstmt.setString(4, cliente.getContacto());
            pstmt.setString(5, cliente.getDireccionDespacho());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    public Cliente buscar(int idCliente) {
        String sql = "SELECT * FROM Cliente WHERE idCliente = ?";
        Cliente cliente = null;
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                        rs.getInt("idCliente"),
                        rs.getString("razonSocial"),
                        rs.getString("documento"),
                        rs.getString("contacto"),
                        rs.getString("direccionDespacho")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente: " + e.getMessage());
        }
        return cliente;
    }

    public List<Cliente> listar() {
        String sql = "SELECT * FROM Cliente";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("idCliente"),
                    rs.getString("razonSocial"),
                    rs.getString("documento"),
                    rs.getString("contacto"),
                    rs.getString("direccionDespacho")
                );
                lista.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE Cliente SET razonSocial = ?, documento = ?, contacto = ?, direccionDespacho = ? WHERE idCliente = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getRazonSocial());
            pstmt.setString(2, cliente.getDocumento());
            pstmt.setString(3, cliente.getContacto());
            pstmt.setString(4, cliente.getDireccionDespacho());
            pstmt.setInt(5, cliente.getIdCliente());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idCliente) {
        String sql = "DELETE FROM Cliente WHERE idCliente = ?";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
}