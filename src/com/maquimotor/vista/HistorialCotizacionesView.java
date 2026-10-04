package com.maquimotor.vista;

import com.maquimotor.dao.CotizacionDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class HistorialCotizacionesView extends JFrame {
    private JTable tablaCabecera;
    private JTable tablaDetalle;
    private DefaultTableModel modeloCabecera;
    private DefaultTableModel modeloDetalle;
    private CotizacionDAO cotizacionDAO = new CotizacionDAO();
    private JFrame ventanaPadre;

    public HistorialCotizacionesView(JFrame padre) {
        this.ventanaPadre = padre;
        setTitle("Historial de Cotizaciones y Control de Reservas (48h) - MaquiMotor");
        setSize(1100, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.55);

        // Tabla Superior: Cotizaciones emitidas
        String[] colsCabecera = {"N° Cot.", "Emisión", "Vencimiento (48h)", "Cliente", "RUC / DNI", "Total", "Estado"};
        modeloCabecera = new DefaultTableModel(colsCabecera, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCabecera = new JTable(modeloCabecera);
        tablaCabecera.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel pnlCabecera = new JPanel(new BorderLayout());
        pnlCabecera.setBorder(BorderFactory.createTitledBorder("Cotizaciones Emitidas (Seleccione una fila para ver el detalle técnico)"));
        pnlCabecera.add(new JScrollPane(tablaCabecera), BorderLayout.CENTER);
        splitPane.setTopComponent(pnlCabecera);

        // Tabla Inferior: Detalle de equipos
        String[] colsDetalle = {"Código", "Equipo / Modelo", "Cantidad Reservada", "Precio Unitario", "Subtotal"};
        modeloDetalle = new DefaultTableModel(colsDetalle, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaDetalle = new JTable(modeloDetalle);

        JPanel pnlDetalle = new JPanel(new BorderLayout());
        pnlDetalle.setBorder(BorderFactory.createTitledBorder("Detalle de Maquinaria y Equipos en Reserva"));
        pnlDetalle.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);
        splitPane.setBottomComponent(pnlDetalle);

        add(splitPane, BorderLayout.CENTER);

        // Evento al seleccionar fila
        tablaCabecera.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaCabecera.getSelectedRow() != -1) {
                int idCot = Integer.parseInt(tablaCabecera.getValueAt(tablaCabecera.getSelectedRow(), 0).toString());
                cargarDetalle(idCot);
            }
        });

        // Panel Inferior: Botones de Acción
        JPanel pnlSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        JButton btnPagar = new JButton("Confirmar Pago (Cerrar Venta)");
        btnPagar.setBackground(new Color(220, 245, 220));
        btnPagar.addActionListener(e -> confirmarPagoSeleccionado());
        pnlSur.add(btnPagar);

        JButton btnExportar = new JButton("Exportar Proforma (RF-10)");
        btnExportar.addActionListener(e -> exportarProformaSeleccionada());
        pnlSur.add(btnExportar);

        JButton btnLiberar = new JButton("Liberar Reserva / Anular Cotización");
        btnLiberar.addActionListener(e -> anularCotizacionSeleccionada());
        pnlSur.add(btnLiberar);

        JButton btnCerrar = new JButton("Volver");
        btnCerrar.addActionListener(e -> {
            ventanaPadre.setVisible(true);
            this.dispose();
        });
        pnlSur.add(btnCerrar);

        add(pnlSur, BorderLayout.SOUTH);

        cargarCabeceras();
    }

    private void cargarCabeceras() {
        modeloCabecera.setRowCount(0);
        List<Object[]> lista = cotizacionDAO.listarResumenCotizaciones();
        for (Object[] fila : lista) {
            modeloCabecera.addRow(new Object[]{
                fila[0],
                fila[1],
                fila[2],
                fila[3],
                fila[4],
                "S/. " + String.format("%.2f", (Double) fila[5]),
                fila[6]
            });
        }
    }

    private void cargarDetalle(int idCotizacion) {
        modeloDetalle.setRowCount(0);
        List<Object[]> lista = cotizacionDAO.listarDetallesPorCotizacion(idCotizacion);
        for (Object[] fila : lista) {
            modeloDetalle.addRow(new Object[]{
                fila[0],
                fila[1],
                fila[2],
                "S/. " + String.format("%.2f", (Double) fila[3]),
                "S/. " + String.format("%.2f", (Double) fila[4])
            });
        }
    }

    private void confirmarPagoSeleccionado() {
        int fila = tablaCabecera.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cotización para registrar el pago.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCot = Integer.parseInt(tablaCabecera.getValueAt(fila, 0).toString());
        String estado = tablaCabecera.getValueAt(fila, 6).toString();
        String total = tablaCabecera.getValueAt(fila, 5).toString();

        if (!estado.equalsIgnoreCase("Vigente")) {
            JOptionPane.showMessageDialog(this, "Solo se pueden pagar cotizaciones en estado 'Vigente'. Estado actual: " + estado, "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Confirmar el pago y emisión de comprobante para la cotización N° " + idCot + "?\n" +
                "Monto Total: " + total + "\n\n" +
                "Nota: La maquinaria quedará vendida definitivamente y no volverá al stock general.",
                "Confirmación de Venta Concluida", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            if (cotizacionDAO.confirmarPagoCotizacion(idCot)) {
                JOptionPane.showMessageDialog(this, "¡Pago registrado con éxito!\nLa venta de la cotización N° " + idCot + " ha quedado consolidada.");
                cargarCabeceras();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el pago. Verifique el estado de la cotización.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarProformaSeleccionada() {
        int fila = tablaCabecera.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cotización del historial para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCot = Integer.parseInt(tablaCabecera.getValueAt(fila, 0).toString());
        String emision = tablaCabecera.getValueAt(fila, 1).toString();
        String vencimiento = tablaCabecera.getValueAt(fila, 2).toString();
        String cliente = tablaCabecera.getValueAt(fila, 3).toString();
        String documento = tablaCabecera.getValueAt(fila, 4).toString();
        String estado = tablaCabecera.getValueAt(fila, 6).toString();

        List<Object[]> detalles = cotizacionDAO.listarDetallesPorCotizacion(idCot);
        if (detalles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La cotización seleccionada no contiene detalles.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Cotizacion_MaquiMotor_" + idCot + ".txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
                pw.println("========================================================================");
                pw.println("                   MAQUIMOTOR PERÚ S.A.C. - PROFORMA TÉCNICA             ");
                pw.println("========================================================================");
                pw.println("Cotización N°  : " + idCot);
                pw.println("Cliente        : " + cliente);
                pw.println("RUC / DNI      : " + documento);
                pw.println("Fecha Emisión  : " + emision);
                pw.println("Vencimiento 48h: " + vencimiento);
                pw.println("Estado Reserva : " + estado);
                pw.println("------------------------------------------------------------------------");
                pw.println("CÓDIGO      DESCRIPCIÓN               CANT.    P. UNITARIO    SUBTOTAL");
                pw.println("------------------------------------------------------------------------");

                double subtotal = 0.0;
                for (Object[] d : detalles) {
                    String cod = (String) d[0];
                    String desc = (String) d[1];
                    int cant = (Integer) d[2];
                    double pu = (Double) d[3];
                    double st = (Double) d[4];

                    pw.println(cod + "    " + desc + "    " + cant + "    S/. " + String.format("%.2f", pu) + "    S/. " + String.format("%.2f", st));
                    subtotal += st;
                }

                double igv = subtotal * 0.18;
                double total = subtotal + igv;
                pw.println("------------------------------------------------------------------------");
                pw.println("SUBTOTAL : S/. " + String.format("%.2f", subtotal));
                pw.println("IGV (18%): S/. " + String.format("%.2f", igv));
                pw.println("TOTAL    : S/. " + String.format("%.2f", total));
                pw.println("========================================================================");

                JOptionPane.showMessageDialog(this, "¡Proforma N° " + idCot + " exportada exitosamente!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void anularCotizacionSeleccionada() {
        int fila = tablaCabecera.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cotización para liberar la reserva.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCot = Integer.parseInt(tablaCabecera.getValueAt(fila, 0).toString());
        String estado = tablaCabecera.getValueAt(fila, 6).toString();

        if (!estado.equalsIgnoreCase("Vigente")) {
            JOptionPane.showMessageDialog(this, "Solo se pueden anular cotizaciones en estado 'Vigente'.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Desea anular la cotización N° " + idCot + "?\nLas unidades reservadas retornarán de inmediato al stock disponible de almacén.",
                "Confirmar Liberación de Reserva", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            if (cotizacionDAO.anularYDevolverStock(idCot)) {
                JOptionPane.showMessageDialog(this, "Cotización anulada con éxito. Stock restituido al inventario.");
                cargarCabeceras();
                modeloDetalle.setRowCount(0);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo anular la cotización.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}