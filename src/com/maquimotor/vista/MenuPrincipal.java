package com.maquimotor.vista;

import com.maquimotor.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {
    private Usuario usuarioActual;
    private JButton btnInventario, btnClientes, btnCotizaciones, btnCerrarSesion;

    public MenuPrincipal() {
        this(new Usuario(1, "admin", "admin123", "Administrador"));
    }

    public MenuPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        initComponents();
        aplicarRestriccionRoles();
    }

    private void initComponents() {
        setTitle("MaquiMotor Perú - Panel Principal (" + usuarioActual.getRol() + ")");
        setSize(440, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitulo = new JLabel("SISTEMA DE GESTIÓN MAQUIMOTOR", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setBounds(20, 20, 400, 25);
        add(lblTitulo);

        JLabel lblUser = new JLabel("Sesión: " + usuarioActual.getNombreUsuario() + " | Rol: " + usuarioActual.getRol(), SwingConstants.CENTER);
        lblUser.setForeground(Color.DARK_GRAY);
        lblUser.setBounds(20, 45, 400, 20);
        add(lblUser);

        btnInventario = new JButton("1. Catálogo e Inventario (Stock)");
        btnInventario.setBounds(70, 85, 290, 35);
        btnInventario.addActionListener(e -> {
            new InventarioView(this, this.usuarioActual).setVisible(true);
            this.setVisible(false);
        });
        add(btnInventario);

        btnClientes = new JButton("2. Gestión de Clientes");
        btnClientes.setBounds(70, 135, 290, 35);
        btnClientes.addActionListener(e -> {
            new ClienteView(this).setVisible(true);
            this.setVisible(false);
        });
        add(btnClientes);

        btnCotizaciones = new JButton("3. Crear Cotización");
        btnCotizaciones.setBounds(70, 185, 290, 35);
        btnCotizaciones.addActionListener(e -> {
            new CotizacionView(usuarioActual, this).setVisible(true);
            this.setVisible(false);
        });
        add(btnCotizaciones);

        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(70, 240, 290, 35);
        btnCerrarSesion.addActionListener(e -> {
            new LoginView().setVisible(true);
            this.dispose();
        });
        add(btnCerrarSesion);
    }

    private void aplicarRestriccionRoles() {
        String rol = usuarioActual.getRol();
        if ("Jefe de Almacén".equalsIgnoreCase(rol)) {
            btnCotizaciones.setEnabled(false);
            btnClientes.setEnabled(false);
        } else if ("Asesor Comercial".equalsIgnoreCase(rol)) {
            btnInventario.setText("1. Consultar Stock Técnico");
        }
    }
}