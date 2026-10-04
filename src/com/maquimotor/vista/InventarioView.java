package com.maquimotor.vista;

import com.maquimotor.dao.EquipoDAO;
import com.maquimotor.modelo.Equipo;
import com.maquimotor.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioView extends JFrame {
    private JTable tablaEquipos;
    private DefaultTableModel modeloTabla;
    private EquipoDAO equipoDAO = new EquipoDAO();
    private JFrame menuPadre;
    private Usuario usuarioLogueado;
    private JComboBox<String> cbFiltroAplicacion;
    private List<Equipo> listaActual = new ArrayList<>();

    public InventarioView(JFrame padre, Usuario usuario) {
        this.menuPadre = padre;
        this.usuarioLogueado = usuario;

        setTitle("Catálogo de Maquinaria y Control de Stock");
        setSize(1020, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Filtros y Alertas
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlNorte.add(new JLabel("Filtrar por Aplicación:"));
        cbFiltroAplicacion = new JComboBox<>(new String[]{"-- Todos --", "Minería", "Agricultura"});
        cbFiltroAplicacion.addActionListener(e -> cargarDatos());
        pnlNorte.add(cbFiltroAplicacion);

        JLabel lblAlerta = new JLabel("  [ROJO: Stock en Umbral Crítico]");
        lblAlerta.setForeground(Color.RED);
        lblAlerta.setFont(new Font("Arial", Font.BOLD, 12));
        pnlNorte.add(lblAlerta);
        add(pnlNorte, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Código", "N° Serie", "Marca/Modelo", "Aplicación", "Precio Base", "Stock Disp.", "Stock Mín.", "Estado", "Ficha Técnica"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaEquipos = new JTable(modeloTabla);
        tablaEquipos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        aplicarAlertaVisualStock();
        cargarDatos();
        add(new JScrollPane(tablaEquipos), BorderLayout.CENTER);

        // Panel Inferior: Botones de Acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));

        JButton btnNuevo = new JButton("+ Registrar");
        btnNuevo.addActionListener(e -> new RegistroEquipoDialog(this, equipoDAO, this::cargarDatos).setVisible(true));
        panelBotones.add(btnNuevo);

        JButton btnEditar = new JButton("Modificar Seleccionado");
        btnEditar.addActionListener(e -> modificarSeleccionado());
        panelBotones.add(btnEditar);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        panelBotones.add(btnEliminar);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> {
            menuPadre.setVisible(true);
            this.dispose();
        });
        panelBotones.add(btnVolver);

        // RF-01: Si no es Administrador ni Jefe de Almacén, deshabilitar acciones de escritura
        if (usuarioLogueado != null && usuarioLogueado.getRol().equalsIgnoreCase("Asesor Comercial")) {
            btnNuevo.setEnabled(false);
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnNuevo.setToolTipText("Acción restringida para el rol Asesor Comercial.");
            btnEditar.setToolTipText("Acción restringida para el rol Asesor Comercial.");
            btnEliminar.setToolTipText("Acción restringida para el rol Asesor Comercial.");
        }

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        listaActual.clear();
        String filtro = (String) cbFiltroAplicacion.getSelectedItem();
        List<Equipo> lista = equipoDAO.listar();

        for (Equipo eq : lista) {
            if (filtro != null && !filtro.equals("-- Todos --") && !eq.getAplicacion().equalsIgnoreCase(filtro)) {
                continue;
            }
            listaActual.add(eq);
            modeloTabla.addRow(new Object[]{
                eq.getIdEquipo(),
                eq.getCodigo(),
                eq.getNumeroSerie(),
                eq.getMarca() + " " + eq.getModelo(),
                eq.getAplicacion(),
                "S/. " + String.format("%.2f", eq.getPrecioBase()),
                eq.getStockDisponible(),
                eq.getStockMinimo(),
                eq.getEstado(),
                eq.getFichaTecnica() != null ? eq.getFichaTecnica().getResumen() : "N/A"
            });
        }
    }

    private void modificarSeleccionado() {
        int fila = tablaEquipos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un equipo de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Equipo seleccionado = listaActual.get(fila);
        new EditarEquipoDialog(this, equipoDAO, seleccionado, this::cargarDatos).setVisible(true);
    }

    private void eliminarSeleccionado() {
        int fila = tablaEquipos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un equipo de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Equipo seleccionado = listaActual.get(fila);
        int resp = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el equipo " + seleccionado.getCodigo() + " (" + seleccionado.getMarca() + ")?",
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            if (equipoDAO.eliminar(seleccionado.getIdEquipo())) {
                JOptionPane.showMessageDialog(this, "Equipo eliminado correctamente.");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar el equipo porque ya cuenta con cotizaciones asociadas.", "Restricción de Integridad", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aplicarAlertaVisualStock() {
        tablaEquipos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                try {
                    int stockDisp = Integer.parseInt(table.getValueAt(row, 6).toString());
                    int stockMin = Integer.parseInt(table.getValueAt(row, 7).toString());

                    if (stockDisp <= stockMin) {
                        c.setBackground(new Color(255, 205, 205));
                        c.setForeground(new Color(150, 0, 0));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground());
                    }
                } catch (Exception ignored) {}
                return c;
            }
        });
    }
}