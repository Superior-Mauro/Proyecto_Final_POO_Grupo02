package com.maquimotor.vista;

import com.maquimotor.dao.ClienteDAO;
import com.maquimotor.dao.CotizacionDAO;
import com.maquimotor.dao.EquipoDAO;
import com.maquimotor.modelo.Cliente;
import com.maquimotor.modelo.Cotizacion;
import com.maquimotor.modelo.DetalleCotizacion;
import com.maquimotor.modelo.Equipo;
import com.maquimotor.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CotizacionView extends JFrame {
    private JTextField txtIdCotizacion;
    private JComboBox<Cliente> cbClientes;
    private JComboBox<Equipo> cbEquipos;
    private JTextField txtCantidad;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTabla;
    private JLabel lblSubtotal, lblIgv, lblTotal;

    private List<DetalleCotizacion> listaDetalles = new ArrayList<>();
    private CotizacionDAO cotizacionDAO = new CotizacionDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private EquipoDAO equipoDAO = new EquipoDAO();
    private Usuario usuarioActual;
    private JFrame menuPadre;

    public CotizacionView() {
        this(null, null);
    }

    public CotizacionView(Usuario usuario, JFrame padre) {
        this.usuarioActual = usuario;
        this.menuPadre = padre;
        initComponents();
        cargarCombos();
    }

    private void initComponents() {
        setTitle("MaquiMotor Perú - Emisión Técnica de Cotizaciones");
        setSize(960, 590);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lId = new JLabel("N° Cotización:"); lId.setBounds(30, 20, 100, 25); add(lId);
        txtIdCotizacion = new JTextField(); txtIdCotizacion.setBounds(130, 20, 120, 25); add(txtIdCotizacion);

        JLabel lCli = new JLabel("Cliente (RUC/Razon):"); lCli.setBounds(280, 20, 140, 25); add(lCli);
        cbClientes = new JComboBox<>(); cbClientes.setBounds(420, 20, 470, 25); add(cbClientes);

        JLabel lEq = new JLabel("Seleccionar Motor:"); lEq.setBounds(30, 65, 120, 25); add(lEq);
        cbEquipos = new JComboBox<>(); cbEquipos.setBounds(150, 65, 430, 25); add(cbEquipos);

        JLabel lCant = new JLabel("Cantidad:"); lCant.setBounds(595, 65, 65, 25); add(lCant);
        txtCantidad = new JTextField(); txtCantidad.setBounds(660, 65, 50, 25); add(txtCantidad);

        JButton btnAgregar = new JButton("+ Agregar");
        btnAgregar.setBounds(720, 63, 90, 28);
        btnAgregar.addActionListener(e -> agregarEquipoADetalle());
        add(btnAgregar);

        JButton btnQuitar = new JButton("- Quitar");
        btnQuitar.setBounds(815, 63, 80, 28);
        btnQuitar.setToolTipText("Quitar ítem seleccionado de la cotización");
        btnQuitar.addActionListener(e -> quitarEquipoDeDetalle());
        add(btnQuitar);

        String[] cols = {"Código", "Descripción", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaDetalles = new JTable(modeloTabla);
        tablaDetalles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Atajo: si presionas la tecla Supr/Delete en la tabla, también se elimina la fila
        tablaDetalles.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    quitarEquipoDeDetalle();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaDetalles);
        scroll.setBounds(30, 105, 880, 245);
        add(scroll);

        lblSubtotal = new JLabel("Subtotal: S/. 0.00"); lblSubtotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblSubtotal.setBounds(690, 360, 220, 22); add(lblSubtotal);

        lblIgv = new JLabel("IGV (18%): S/. 0.00"); lblIgv.setFont(new Font("Arial", Font.BOLD, 12));
        lblIgv.setBounds(690, 385, 220, 22); add(lblIgv);

        lblTotal = new JLabel("Total: S/. 0.00"); lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(new Color(0, 102, 204));
        lblTotal.setBounds(690, 410, 220, 25); add(lblTotal);

        // Botones inferiores de acción (Distribución limpia de 3 botones)
        JButton btnGuardar = new JButton("Procesar y Reservar (RF-08)");
        btnGuardar.setBounds(30, 470, 240, 36);
        btnGuardar.addActionListener(e -> procesarCotizacion());
        add(btnGuardar);

        JButton btnHistorial = new JButton("Historial / Reservas (48h)");
        btnHistorial.setBounds(350, 470, 240, 36);
        btnHistorial.addActionListener(e -> {
            new HistorialCotizacionesView(this).setVisible(true);
            this.setVisible(false);
        });
        add(btnHistorial);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.setBounds(670, 470, 240, 36);
        btnVolver.addActionListener(e -> {
            if (menuPadre != null) menuPadre.setVisible(true);
            else new MenuPrincipal().setVisible(true);
            this.dispose();
        });
        add(btnVolver);
    }

    private void cargarCombos() {
        cotizacionDAO.liberarCotizacionesExpiradas();
        // Asignar automáticamente el siguiente correlativo libre
        txtIdCotizacion.setText(String.valueOf(cotizacionDAO.obtenerSiguienteIdCotizacion()));

        cbClientes.removeAllItems();
        for (Cliente c : clienteDAO.listar()) {
            cbClientes.addItem(c);
        }
        cbClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Cliente) {
                    Cliente cl = (Cliente) value;
                    setText(cl.getDocumento() + " - " + cl.getRazonSocial());
                }
                return this;
            }
        });

        cbEquipos.removeAllItems();
        for (Equipo eq : equipoDAO.listar()) {
            if ("Disponible".equalsIgnoreCase(eq.getEstado()) && eq.getStockDisponible() > 0) {
                cbEquipos.addItem(eq);
            }
        }
    }

    private void agregarEquipoADetalle() {
        Equipo seleccionado = (Equipo) cbEquipos.getSelectedItem();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un equipo disponible.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            if (cant <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Calcular cuánto ya se agregó de este mismo equipo en la tabla
            int yaCotizado = 0;
            for (DetalleCotizacion d : listaDetalles) {
                if (d.getEquipo().getIdEquipo() == seleccionado.getIdEquipo()) {
                    yaCotizado += d.getCantidad();
                }
            }

            if ((yaCotizado + cant) > seleccionado.getStockDisponible()) {
                JOptionPane.showMessageDialog(this, "⚠️ Stock insuficiente para el equipo seleccionado.\n" +
                        "Disponible actualmente: " + seleccionado.getStockDisponible() + " unidades.\n" +
                        (yaCotizado > 0 ? "Ya agregaste en esta cotización: " + yaCotizado + " unidades." : ""),
                        "Stock Insuficiente", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double subtotalItem = cant * seleccionado.getPrecioBase();
            DetalleCotizacion detalle = new DetalleCotizacion(0, 0, seleccionado, cant, seleccionado.getPrecioBase(), subtotalItem);
            listaDetalles.add(detalle);

            modeloTabla.addRow(new Object[]{
                seleccionado.getCodigo(),
                seleccionado.getMarca() + " " + seleccionado.getModelo(),
                cant,
                "S/. " + String.format("%.2f", seleccionado.getPrecioBase()),
                "S/. " + String.format("%.2f", subtotalItem)
            });

            recalcularTotales();
            txtCantidad.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad entera válida.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void quitarEquipoDeDetalle() {
        int fila = tablaDetalles.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione la fila que desea retirar de la cotización.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        listaDetalles.remove(fila);
        modeloTabla.removeRow(fila);
        recalcularTotales();
    }

    private void recalcularTotales() {
        double subtotal = 0.0;
        for (DetalleCotizacion d : listaDetalles) {
            subtotal += d.getSubtotalItem();
        }
        double igv = subtotal * 0.18;
        double total = subtotal + igv;

        lblSubtotal.setText("Subtotal: S/. " + String.format("%.2f", subtotal));
        lblIgv.setText("IGV (18%): S/. " + String.format("%.2f", igv));
        lblTotal.setText("Total: S/. " + String.format("%.2f", total));
    }

    private void procesarCotizacion() {
        if (txtIdCotizacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el N° de Cotización.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (listaDetalles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe añadir al menos un equipo para generar la cotización.", "Sin Detalles", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idCot = Integer.parseInt(txtIdCotizacion.getText().trim());
            Cliente cl = (Cliente) cbClientes.getSelectedItem();
            if (cl == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double subtotal = 0.0;
            for (DetalleCotizacion d : listaDetalles) subtotal += d.getSubtotalItem();
            double igv = subtotal * 0.18;
            double total = subtotal + igv;

            int idUsr = (usuarioActual != null && usuarioActual.getIdUsuario() > 0) ? usuarioActual.getIdUsuario() : 1;
            Cotizacion cot = new Cotizacion(idCot, cl.getIdCliente(), idUsr, subtotal, igv, total);

            boolean ok = cotizacionDAO.registrarCotizacionCompleta(cot, listaDetalles);
            if (ok) {
                JOptionPane.showMessageDialog(this, "¡Cotización N° " + idCot + " procesada con éxito!\nLas unidades han quedado reservadas por 48 horas.");

                // Confirmación para exportar antes de vaciar la tabla
                int respExportar = JOptionPane.showConfirmDialog(this,
                        "¿Desea exportar la Proforma Técnica en archivo de texto ahora?",
                        "Exportar Proforma", JOptionPane.YES_NO_OPTION);

                if (respExportar == JOptionPane.YES_OPTION) {
                    exportarProforma();
                }

                // Limpieza de campos para la siguiente operación
                listaDetalles.clear();
                modeloTabla.setRowCount(0);
                recalcularTotales();
                txtCantidad.setText("");
                cargarCombos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo procesar la cotización.\nVerifique si el N° " + idCot + " ya existe o si las unidades superaron el stock.", "Aviso", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID de la cotización debe ser un número entero.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exportarProforma() {
        if (listaDetalles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos que exportar. Agregue productos a la cotización.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Cliente cl = (Cliente) cbClientes.getSelectedItem();
        String numCot = txtIdCotizacion.getText().trim();
        if (numCot.isEmpty()) numCot = "102";

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Cotizacion_MaquiMotor_" + numCot + ".txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
                pw.println("========================================================================");
                pw.println("                   MAQUIMOTOR PERÚ S.A.C. - PROFORMA TÉCNICA             ");
                pw.println("========================================================================");
                pw.println("Cotización N°: " + numCot);
                pw.println("Cliente      : " + (cl != null ? cl.getRazonSocial() : "N/A"));
                pw.println("RUC/DNI      : " + (cl != null ? cl.getDocumento() : "N/A"));
                pw.println("Dirección    : " + (cl != null ? cl.getDireccionDespacho() : "N/A"));
                pw.println("Vigencia     : 48 horas (Unidades en reserva)");
                pw.println("------------------------------------------------------------------------");
                pw.println("CÓDIGO      DESCRIPCIÓN               CANT.    P. UNITARIO    SUBTOTAL");
                pw.println("------------------------------------------------------------------------");

                double sub = 0.0;
                for (DetalleCotizacion d : listaDetalles) {
                    String cod = d.getEquipo().getCodigo();
                    String desc = d.getEquipo().getMarca() + " " + d.getEquipo().getModelo();
                    int cant = d.getCantidad();
                    double pu = d.getPrecioUnitario();
                    double st = d.getSubtotalItem();

                    pw.println(cod + "    " + desc + "    " + cant + "    S/. " + String.format("%.2f", pu) + "    S/. " + String.format("%.2f", st));

                    if (d.getEquipo().getFichaTecnica() != null) {
                        pw.println("   [Ficha Técnica: " + d.getEquipo().getFichaTecnica().getResumen() + "]");
                    }
                    sub += st;
                }

                double igv = sub * 0.18;
                double tot = sub + igv;
                pw.println("------------------------------------------------------------------------");
                pw.println("SUBTOTAL : S/. " + String.format("%.2f", sub));
                pw.println("IGV (18%): S/. " + String.format("%.2f", igv));
                pw.println("TOTAL    : S/. " + String.format("%.2f", tot));
                pw.println("========================================================================");

                JOptionPane.showMessageDialog(this, "¡Proforma técnica generada y exportada con éxito!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}