package com.maquimotor.vista;

import javax.swing.*;

public class MenuPrincipal extends JFrame {

    private JButton btnClientes;
    private JButton btnCotizaciones;
    private JButton btnSalir;

    public MenuPrincipal() {
        initComponents();
    }

    private void initComponents() {
        setTitle("MaquiMotor Perú S.A.C. - Sistema Principal");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitulo = new JLabel("GESTIÓN MAQUIMOTOR", SwingConstants.CENTER);
        lblTitulo.setBounds(50, 30, 300, 25);
        add(lblTitulo);

        // Botón 1: Abre la ventana de Clientes
        btnClientes = new JButton("1. Gestionar Clientes");
        btnClientes.setBounds(80, 80, 240, 35);
        btnClientes.addActionListener(e -> {
            new ClienteView().setVisible(true);
        });
        add(btnClientes);

        // Botón 2: Abre la ventana de Cotizaciones
        btnCotizaciones = new JButton("2. Crear Cotización");
        btnCotizaciones.setBounds(80, 135, 240, 35);
        btnCotizaciones.addActionListener(e -> {
            new CotizacionView().setVisible(true);
        });
        add(btnCotizaciones);

        // Botón 3: Salir del sistema
        btnSalir = new JButton("Salir");
        btnSalir.setBounds(80, 190, 240, 35);
        btnSalir.addActionListener(e -> System.exit(0));
        add(btnSalir);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}