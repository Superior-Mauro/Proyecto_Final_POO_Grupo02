package com.maquimotor.vista;

import com.maquimotor.dao.ClienteDAO;
import com.maquimotor.modelo.Cliente;

import javax.swing.*;

public class ClienteView extends JFrame {
    private JTextField txtIdCliente, txtRazonSocial, txtRuc, txtContacto, txtDireccion;
    private ClienteDAO clienteDAO;

    public ClienteView() {
        clienteDAO = new ClienteDAO();
        setTitle("Gestión de Clientes");
        setSize(400, 420);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel l1 = new JLabel("ID Cliente:");
        l1.setBounds(20, 20, 100, 25); add(l1);
        txtIdCliente = new JTextField(); txtIdCliente.setBounds(130, 20, 200, 25); add(txtIdCliente);

        JLabel l2 = new JLabel("Razón Social:");
        l2.setBounds(20, 60, 100, 25); add(l2);
        txtRazonSocial = new JTextField(); txtRazonSocial.setBounds(130, 60, 200, 25); add(txtRazonSocial);

        JLabel l3 = new JLabel("RUC:");
        l3.setBounds(20, 100, 100, 25); add(l3);
        txtRuc = new JTextField(); txtRuc.setBounds(130, 100, 200, 25); add(txtRuc);

        JLabel l4 = new JLabel("Contacto:");
        l4.setBounds(20, 140, 100, 25); add(l4);
        txtContacto = new JTextField(); txtContacto.setBounds(130, 140, 200, 25); add(txtContacto);

        JLabel l5 = new JLabel("Dirección:");
        l5.setBounds(20, 180, 100, 25); add(l5);
        txtDireccion = new JTextField(); txtDireccion.setBounds(130, 180, 200, 25); add(txtDireccion);

        JButton btnRegistrar = new JButton("Registrar Cliente");
        btnRegistrar.setBounds(110, 230, 170, 30);
        add(btnRegistrar);

        JButton btnVolver = new JButton("Menú Principal");
        btnVolver.setBounds(110, 275, 170, 30);
        add(btnVolver);

        // Eventos
        btnRegistrar.addActionListener(e -> registrarCliente());
        btnVolver.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            this.dispose();
        });
    }

    private void registrarCliente() {
        // Validar que los campos no estén vacíos
        if (txtIdCliente.getText().trim().isEmpty() ||
            txtRazonSocial.getText().trim().isEmpty() ||
            txtRuc.getText().trim().isEmpty() ||
            txtContacto.getText().trim().isEmpty() ||
            txtDireccion.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "⚠️ Por favor, complete todos los campos obligatorios antes de registrar.", 
                "Campos Incompletos", 
                JOptionPane.WARNING_MESSAGE);
            return; 
        }

        try {
            int id = Integer.parseInt(txtIdCliente.getText().trim());
            String razonSocial = txtRazonSocial.getText().trim();
            String ruc = txtRuc.getText().trim();
            String contacto = txtContacto.getText().trim();
            String direccion = txtDireccion.getText().trim();

            Cliente cliente = new Cliente(id, razonSocial, ruc, contacto, direccion);
            boolean resultado = clienteDAO.insertar(cliente);

            if (resultado) {
                JOptionPane.showMessageDialog(this, "¡Cliente registrado con éxito!");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al registrar el cliente. Verifique si el ID ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠️ El ID del cliente debe ser un valor numérico válido.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtIdCliente.setText("");
        txtRazonSocial.setText("");
        txtRuc.setText("");
        txtContacto.setText("");
        txtDireccion.setText("");
        txtIdCliente.requestFocus();
    }
}