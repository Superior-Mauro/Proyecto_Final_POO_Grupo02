package com.maquimotor.vista;

import com.maquimotor.dao.ClienteDAO;
import com.maquimotor.modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteView extends JFrame {
    private JTextField txtId, txtRazonSocial, txtDocumento, txtContacto, txtDireccion;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private ClienteDAO clienteDAO = new ClienteDAO();
    private JFrame menuPadre;
    private List<Cliente> listaActual = new ArrayList<>();

    // Componentes para alternar la vista
    private JScrollPane scrollTabla;
    private JButton btnToggleTabla;
    private JButton btnModificar;
    private JButton btnEliminar;
    private boolean tablaVisible = true;

    public ClienteView(JFrame padre) {
        this.menuPadre = padre;
        setTitle("Gestión Comercial de Clientes (RF-05)");
        setSize(880, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Formulario de Registro
        JPanel pnlFormulario = new JPanel(new GridLayout(6, 2, 8, 8));
        pnlFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

        pnlFormulario.add(new JLabel("ID Cliente:"));
        txtId = new JTextField();
        pnlFormulario.add(txtId);

        pnlFormulario.add(new JLabel("Razón Social / Nombre:"));
        txtRazonSocial = new JTextField();
        pnlFormulario.add(txtRazonSocial);

        pnlFormulario.add(new JLabel("RUC (11 dígitos) o DNI (8 dígitos):"));
        txtDocumento = new JTextField();
        pnlFormulario.add(txtDocumento);

        pnlFormulario.add(new JLabel("Persona de Contacto:"));
        txtContacto = new JTextField();
        pnlFormulario.add(txtContacto);

        pnlFormulario.add(new JLabel("Dirección de Despacho:"));
        txtDireccion = new JTextField();
        pnlFormulario.add(txtDireccion);

        JButton btnRegistrar = new JButton("Registrar Cliente");
        btnRegistrar.addActionListener(e -> registrarCliente());
        pnlFormulario.add(btnRegistrar);

        JButton btnLimpiar = new JButton("Limpiar Campos");
        btnLimpiar.addActionListener(e -> limpiarCampos());
        pnlFormulario.add(btnLimpiar);

        add(pnlFormulario, BorderLayout.NORTH);

        // Centro: Tabla con ScrollPane
        String[] columnas = {"ID", "Razón Social", "RUC / DNI", "Contacto", "Dirección de Despacho"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollTabla = new JScrollPane(tablaClientes);
        cargarTabla();
        add(scrollTabla, BorderLayout.CENTER);

        // Panel Inferior: Botones de Acción
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

        btnToggleTabla = new JButton("Ocultar Lista Clientes");
        btnToggleTabla.addActionListener(e -> alternarVisibilidadTabla());
        pnlBotones.add(btnToggleTabla);

        btnModificar = new JButton("Modificar Seleccionado");
        btnModificar.addActionListener(e -> modificarClienteSeleccionado());
        pnlBotones.add(btnModificar);

        btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> eliminarClienteSeleccionado());
        pnlBotones.add(btnEliminar);

        JButton btnVolver = new JButton("Menú Principal");
        btnVolver.addActionListener(e -> {
            menuPadre.setVisible(true);
            this.dispose();
        });
        pnlBotones.add(btnVolver);

        add(pnlBotones, BorderLayout.SOUTH);
    }

    private void alternarVisibilidadTabla() {
        tablaVisible = !tablaVisible;
        scrollTabla.setVisible(tablaVisible);
        btnModificar.setVisible(tablaVisible);
        btnEliminar.setVisible(tablaVisible);

        if (tablaVisible) {
            btnToggleTabla.setText("Ocultar Lista Clientes");
            setSize(880, 580);
            cargarTabla();
        } else {
            btnToggleTabla.setText("Ver Lista Clientes");
            setSize(880, 310);
        }
        revalidate();
        repaint();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        listaActual = clienteDAO.listar();
        for (Cliente c : listaActual) {
            modeloTabla.addRow(new Object[]{
                c.getIdCliente(),
                c.getRazonSocial(),
                c.getDocumento(),
                c.getContacto(),
                c.getDireccionDespacho()
            });
        }
    }

    private void registrarCliente() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String razon = txtRazonSocial.getText().trim();
            String doc = txtDocumento.getText().trim();
            String contacto = txtContacto.getText().trim();
            String dir = txtDireccion.getText().trim();

            if (razon.isEmpty() || doc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete la Razón Social y el Documento.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!doc.matches("^(\\d{8}|\\d{11})$")) {
                JOptionPane.showMessageDialog(this, "El documento debe ser numérico: DNI (8 dígitos) o RUC (11 dígitos).", "Documento Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cliente nuevo = new Cliente(id, razon, doc, contacto, dir);
            if (clienteDAO.insertar(nuevo)) {
                JOptionPane.showMessageDialog(this, "¡Cliente registrado con éxito!");
                limpiarCampos();
                if (tablaVisible) {
                    cargarTabla();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el cliente. Verifique si el ID ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número entero.", "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void modificarClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente actual = listaActual.get(fila);

        JTextField tfRazon = new JTextField(actual.getRazonSocial());
        JTextField tfDoc = new JTextField(actual.getDocumento());
        JTextField tfContacto = new JTextField(actual.getContacto());
        JTextField tfDir = new JTextField(actual.getDireccionDespacho());

        Object[] formulario = {
            "ID Cliente (No editable): " + actual.getIdCliente(),
            "Razón Social:", tfRazon,
            "RUC / DNI:", tfDoc,
            "Contacto:", tfContacto,
            "Dirección:", tfDir
        };

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Modificar Cliente", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            String nuevoDoc = tfDoc.getText().trim();
            if (!nuevoDoc.matches("^(\\d{8}|\\d{11})$")) {
                JOptionPane.showMessageDialog(this, "El documento debe ser numérico: DNI (8) o RUC (11).", "Documento Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            actual.setRazonSocial(tfRazon.getText().trim());
            actual.setDocumento(nuevoDoc);
            actual.setContacto(tfContacto.getText().trim());
            actual.setDireccionDespacho(tfDir.getText().trim());

            if (clienteDAO.actualizar(actual)) {
                JOptionPane.showMessageDialog(this, "¡Cliente modificado correctamente!");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente actual = listaActual.get(fila);
        int resp = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al cliente " + actual.getRazonSocial() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            if (clienteDAO.eliminar(actual.getIdCliente())) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar el cliente porque tiene cotizaciones asociadas (Integridad Referencial).", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtRazonSocial.setText("");
        txtDocumento.setText("");
        txtContacto.setText("");
        txtDireccion.setText("");
    }
}