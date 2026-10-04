package com.maquimotor.vista;

import com.maquimotor.dao.UsuarioDAO;
import com.maquimotor.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginView() {
        setTitle("MaquiMotor Perú - Control de Acceso");
        setSize(360, 240);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        JLabel lblTitle = new JLabel("INICIAR SESIÓN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBounds(30, 15, 300, 25);
        add(lblTitle);

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(35, 60, 80, 25);
        add(lblUser);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(120, 60, 185, 25);
        add(txtUsuario);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(35, 100, 80, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(120, 100, 185, 25);
        add(txtPassword);

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBounds(110, 150, 140, 32);
        btnIngresar.addActionListener(e -> autenticar());
        add(btnIngresar);
    }

    private void autenticar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete usuario y contraseña.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = usuarioDAO.autenticar(user, pass);
        if (u != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido, " + u.getNombreUsuario() + " [" + u.getRol() + "]");
            new MenuPrincipal(u).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales inválidas. Verifique sus datos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
        }
    }
}