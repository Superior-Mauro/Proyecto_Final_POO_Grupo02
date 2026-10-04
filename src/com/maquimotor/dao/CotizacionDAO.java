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
import java.sql.Statement;

public class CotizacionDAO {

    // RF-08: Libera automáticamente reservas que superen las 48 horas
    public void liberarCotizacionesExpiradas() {
        String sqlBuscarExpiradas = 
            "SELECT idCotizacion FROM Cotizacion WHERE estado = 'Vigente' AND fechaVencimiento <= GETDATE()";
        String sqlDetalles = "SELECT idEquipo, cantidad FROM DetalleCotizacion WHERE idCotizacion = ?";
        String sqlDevolverStock = 
            "UPDATE Equipo SET stockDisponible = stockDisponible + ?, " +
            "estado = CASE WHEN estado = 'Reservado' THEN 'Disponible' ELSE estado END " +
            "WHERE idEquipo = ?";
        String sqlMarcarExpirada = "UPDATE Cotizacion SET estado = 'Expirada' WHERE idCotizacion = ?";

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            if (con == null) return;
            con.setAutoCommit(false);

            List<Integer> idsExpirados = new ArrayList<>();
            try (PreparedStatement psExp = con.prepareStatement(sqlBuscarExpiradas);
                 ResultSet rsExp = psExp.executeQuery()) {
                while (rsExp.next()) {
                    idsExpirados.add(rsExp.getInt("idCotizacion"));
                }
            }

            for (int idCot : idsExpirados) {
                try (PreparedStatement psDet = con.prepareStatement(sqlDetalles)) {
                    psDet.setInt(1, idCot);
                    try (ResultSet rsDet = psDet.executeQuery()) {
                        while (rsDet.next()) {
                            int idEquipo = rsDet.getInt("idEquipo");
                            int cant = rsDet.getInt("cantidad");
                            try (PreparedStatement psStock = con.prepareStatement(sqlDevolverStock)) {
                                psStock.setInt(1, cant);
                                psStock.setInt(2, idEquipo);
                                psStock.executeUpdate();
                            }
                        }
                    }
                }
                try (PreparedStatement psUpd = con.prepareStatement(sqlMarcarExpirada)) {
                    psUpd.setInt(1, idCot);
                    psUpd.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            System.err.println("Error al liberar cotizaciones expiradas: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {}
            }
        }
    }

// Obtiene el siguiente ID correlativo para la cotización
    public int obtenerSiguienteIdCotizacion() {
        String sql = "SELECT ISNULL(MAX(idCotizacion), 100) + 1 AS siguiente FROM Cotizacion";
        Connection con = ConexionDB.conectar();
        if (con == null) return 101;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("siguiente");
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular siguiente ID de cotización: " + e.getMessage());
        } finally {
            try { con.close(); } catch (SQLException ignored) {}
        }
        return 101;
    }
// RF-08: Registra cotización con reserva a 48 horas y descuenta stock atómicamente
    public boolean registrarCotizacionCompleta(Cotizacion cot, List<DetalleCotizacion> detalles) {
        String sqlCot = "INSERT INTO Cotizacion (idCotizacion, idCliente, idUsuario, fechaEmision, fechaVencimiento, subtotal, igv, total, estado) " +
                        "VALUES (?, ?, ?, GETDATE(), DATEADD(hour, 48, GETDATE()), ?, ?, ?, 'Vigente')";
        String sqlDet = "INSERT INTO DetalleCotizacion (idCotizacion, idEquipo, cantidad, precioUnitario, subtotalItem) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE Equipo SET stockDisponible = stockDisponible - ?, " +
                          "estado = CASE WHEN (stockDisponible - ?) <= 0 THEN 'Reservado' ELSE estado END " +
                          "WHERE idEquipo = ? AND stockDisponible >= ?";

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            if (con == null) {
                System.err.println("❌ No se pudo conectar a la base de datos.");
                return false;
            }
            con.setAutoCommit(false);

            // 1. Obtener un idUsuario válido si no viene seteado
            int idUsuarioValido = cot.getIdUsuario();
            if (idUsuarioValido <= 0) {
                try (Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT TOP 1 idUsuario FROM Usuario ORDER BY idUsuario ASC")) {
                    if (rs.next()) {
                        idUsuarioValido = rs.getInt(1);
                    } else {
                        idUsuarioValido = 1;
                    }
                }
            }

            // 2. Insertar Cabecera
            try (PreparedStatement pstCot = con.prepareStatement(sqlCot)) {
                pstCot.setInt(1, cot.getIdCotizacion());
                pstCot.setInt(2, cot.getIdCliente());
                pstCot.setInt(3, idUsuarioValido);
                pstCot.setDouble(4, cot.getSubtotal());
                pstCot.setDouble(5, cot.getIgv());
                pstCot.setDouble(6, cot.getTotal());
                pstCot.executeUpdate();
            }

            // 3. Insertar Detalles y Descontar Stock
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
                        System.err.println("❌ Stock insuficiente en BD para el equipo ID: " + d.getEquipo().getIdEquipo());
                        con.rollback();
                        return false;
                    }
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error SQL exacto en transacción de cotización: " + e.getMessage());
            e.printStackTrace();
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

    // RF-09: Confirma el pago de la cotización, cerrando la venta definitivamente
    public boolean confirmarPagoCotizacion(int idCotizacion) {
        String sqlValidar = "SELECT estado FROM Cotizacion WHERE idCotizacion = ?";
        String sqlPagar = "UPDATE Cotizacion SET estado = 'Pagada' WHERE idCotizacion = ? AND estado = 'Vigente'";
        String sqlDetalles = "SELECT idEquipo FROM DetalleCotizacion WHERE idCotizacion = ?";
        String sqlActualizarEquipo = 
            "UPDATE Equipo SET estado = CASE WHEN stockDisponible <= 0 THEN 'Vendido' ELSE estado END WHERE idEquipo = ?";

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            con.setAutoCommit(false);

            // 1. Marcar cotización como 'Pagada'
            try (PreparedStatement psPagar = con.prepareStatement(sqlPagar)) {
                psPagar.setInt(1, idCotizacion);
                int filas = psPagar.executeUpdate();
                if (filas == 0) {
                    con.rollback();
                    return false; // Ya no estaba vigente (estaba anulada o expirada)
                }
            }

            // 2. Si el stock disponible llegó a 0, actualizar el estado del equipo a 'Vendido'
            try (PreparedStatement psDet = con.prepareStatement(sqlDetalles)) {
                psDet.setInt(1, idCotizacion);
                try (ResultSet rs = psDet.executeQuery()) {
                    while (rs.next()) {
                        int idEq = rs.getInt("idEquipo");
                        try (PreparedStatement psEq = con.prepareStatement(sqlActualizarEquipo)) {
                            psEq.setInt(1, idEq);
                            psEq.executeUpdate();
                        }
                    }
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al confirmar pago de cotización: " + e.getMessage());
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

    // Permite anular manualmente una cotización vigente y reponer el stock
    public boolean anularYDevolverStock(int idCotizacion) {
        String sqlDetalles = "SELECT idEquipo, cantidad FROM DetalleCotizacion WHERE idCotizacion = ?";
        String sqlDevolverStock = 
            "UPDATE Equipo SET stockDisponible = stockDisponible + ?, " +
            "estado = CASE WHEN estado = 'Reservado' THEN 'Disponible' ELSE estado END " +
            "WHERE idEquipo = ?";
        String sqlAnular = "UPDATE Cotizacion SET estado = 'Anulada' WHERE idCotizacion = ? AND estado = 'Vigente'";

        Connection con = null;
        try {
            con = ConexionDB.conectar();
            if (con == null) return false;
            con.setAutoCommit(false);

            try (PreparedStatement psDet = con.prepareStatement(sqlDetalles)) {
                psDet.setInt(1, idCotizacion);
                try (ResultSet rs = psDet.executeQuery()) {
                    while (rs.next()) {
                        int idEquipo = rs.getInt("idEquipo");
                        int cant = rs.getInt("cantidad");
                        try (PreparedStatement psStock = con.prepareStatement(sqlDevolverStock)) {
                            psStock.setInt(1, cant);
                            psStock.setInt(2, idEquipo);
                            psStock.executeUpdate();
                        }
                    }
                }
            }

            try (PreparedStatement psAnu = con.prepareStatement(sqlAnular)) {
                psAnu.setInt(1, idCotizacion);
                int afect = psAnu.executeUpdate();
                if (afect == 0) {
                    con.rollback();
                    return false;
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) {}
            }
            System.err.println("Error al anular cotización: " + e.getMessage());
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) {}
            }
        }
    }

    // Listar resumen con cliente y fecha de vencimiento a 48h
    public List<Object[]> listarResumenCotizaciones() {
        liberarCotizacionesExpiradas();
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT c.idCotizacion, c.fechaEmision, c.fechaVencimiento, cl.razonSocial, cl.documento, c.total, c.estado " +
                     "FROM Cotizacion c " +
                     "INNER JOIN Cliente cl ON c.idCliente = cl.idCliente " +
                     "ORDER BY c.idCotizacion DESC";

        Connection con = ConexionDB.conectar();
        if (con == null) return lista;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("idCotizacion"),
                    rs.getTimestamp("fechaEmision"),
                    rs.getTimestamp("fechaVencimiento"),
                    rs.getString("razonSocial"),
                    rs.getString("documento"),
                    rs.getDouble("total"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al listar cotizaciones: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException ignored) {}
            }
        }
        return lista;
    }

    // Listar detalle de equipos para una cotización seleccionada
    public List<Object[]> listarDetallesPorCotizacion(int idCotizacion) {
        List<Object[]> detalles = new ArrayList<>();
        String sql = "SELECT e.codigo, e.marca + ' ' + e.modelo AS descripcion, d.cantidad, d.precioUnitario, d.subtotalItem " +
                     "FROM DetalleCotizacion d " +
                     "INNER JOIN Equipo e ON d.idEquipo = e.idEquipo " +
                     "WHERE d.idCotizacion = ?";

        Connection con = ConexionDB.conectar();
        if (con == null) return detalles;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCotizacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new Object[]{
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precioUnitario"),
                        rs.getDouble("subtotalItem")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar detalles: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException ignored) {}
            }
        }
        return detalles;
    }

    public List<Cotizacion> listar() {
        String sql = "SELECT idCotizacion, idCliente, idUsuario, subtotal, igv, total, estado FROM Cotizacion";
        List<Cotizacion> lista = new ArrayList<>();
        Connection con = ConexionDB.conectar();
        if (con == null) return lista;

        try (PreparedStatement pstmt = con.prepareStatement(sql);
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
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException ignored) {}
            }
        }
        return lista;
    }
}