package com.maquimotor.vista;

import com.maquimotor.dao.CotizacionDAO;
import com.maquimotor.modelo.Cotizacion;

import javax.swing.*;

public class CotizacionView extends JFrame {
    
    private JTextField txtIdCotizacion;
    private JTextField txtIdCliente;
    private JTextField txtPrecioUnitario;
    private JTextField txtCantidad;
    
    private JLabel lblSubtotal;
    private JLabel lblIgv;
    private JLabel lblTotal;
    
    private JButton btnCalcular;
    private JButton btnGuardar;
    private JButton btnVolver;
    
    private CotizacionDAO cotizacionDAO;

    public CotizacionView() {
        cotizacionDAO = new CotizacionDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("MaquiMotor Perú S.A.C. - Gestión de Cotizaciones");
        setSize(430, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // ID Cotización
        JLabel lblCot = new JLabel("ID Cotización:");
        lblCot.setBounds(30, 30, 120, 25);
        add(lblCot);

        txtIdCotizacion = new JTextField();
        txtIdCotizacion.setBounds(150, 30, 210, 25);
        add(txtIdCotizacion);

        // ID Cliente
        JLabel lblCli = new JLabel("ID Cliente:");
        lblCli.setBounds(30, 70, 120, 25);
        add(lblCli);

        txtIdCliente = new JTextField();
        txtIdCliente.setBounds(150, 70, 210, 25);
        add(txtIdCliente);

        // Precio Unitario
        JLabel lblPrecio = new JLabel("Precio Unitario (S/.):");
        lblPrecio.setBounds(30, 110, 130, 25);
        add(lblPrecio);

        txtPrecioUnitario = new JTextField();
        txtPrecioUnitario.setBounds(150, 110, 210, 25);
        add(txtPrecioUnitario);

        // Cantidad
        JLabel lblCant = new JLabel("Cantidad:");
        lblCant.setBounds(30, 150, 120, 25);
        add(lblCant);

        txtCantidad = new JTextField();
        txtCantidad.setBounds(150, 150, 210, 25);
        add(txtCantidad);

        // Etiquetas de Resultados / Totales
        lblSubtotal = new JLabel("Subtotal: S/. 0.00");
        lblSubtotal.setBounds(30, 200, 300, 25);
        add(lblSubtotal);

        lblIgv = new JLabel("IGV (18%): S/. 0.00");
        lblIgv.setBounds(30, 230, 300, 25);
        add(lblIgv);

        lblTotal = new JLabel("Total: S/. 0.00");
        lblTotal.setBounds(30, 260, 300, 25);
        add(lblTotal);

        // Botón Calcular
        btnCalcular = new JButton("Calcular");
        btnCalcular.setBounds(30, 310, 355, 30);
        btnCalcular.addActionListener(e -> calcularTotales());
        add(btnCalcular);

        // Botón Guardar Cotización
        btnGuardar = new JButton("Guardar Cotización");
        btnGuardar.setBounds(30, 355, 170, 35);
        btnGuardar.addActionListener(e -> registrarCotizacion());
        add(btnGuardar);

        // Botón para volver al Menú Principal
        btnVolver = new JButton("Menú Principal");
        btnVolver.setBounds(215, 355, 170, 35);
        btnVolver.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            this.dispose();
        });
        add(btnVolver);
    }

    private boolean validarCampos() {
        if (txtIdCotizacion.getText().trim().isEmpty() || 
            txtIdCliente.getText().trim().isEmpty() || 
            txtPrecioUnitario.getText().trim().isEmpty() || 
            txtCantidad.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "¡Advertencia! Todos los campos son obligatorios. Por favor, complete los recuadros vacíos.", 
                "Campos Incompletos", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void calcularTotales() {
        if (!validarCampos()) return;

        try {
            double precio = Double.parseDouble(txtPrecioUnitario.getText().trim());
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());

            double subtotal = cantidad * precio;
            double igv = subtotal * 0.18;
            double total = subtotal + igv;

            lblSubtotal.setText("Subtotal: S/. " + String.format("%.2f", subtotal));
            lblIgv.setText("IGV (18%): S/. " + String.format("%.2f", igv));
            lblTotal.setText("Total: S/. " + String.format("%.2f", total));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique que el precio y la cantidad contengan valores numéricos válidos.", "Formato incorrecto", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarCotizacion() {
        if (!validarCampos()) return;

        try {
            // 1. Obtener datos de los campos de texto
            int idCot = Integer.parseInt(txtIdCotizacion.getText().trim());
            int idCli = Integer.parseInt(txtIdCliente.getText().trim());
            
            double cantidad = Double.parseDouble(txtCantidad.getText().trim());
            double precioUnitario = Double.parseDouble(txtPrecioUnitario.getText().trim());

            // 2. Realizar cálculos
            double subtotal = cantidad * precioUnitario;
            double igv = subtotal * 0.18; // 18% de IGV
            double total = subtotal + igv;

            // Actualizar etiquetas en pantalla por si no presionó calcular antes
            lblSubtotal.setText("Subtotal: S/. " + String.format("%.2f", subtotal));
            lblIgv.setText("IGV (18%): S/. " + String.format("%.2f", igv));
            lblTotal.setText("Total: S/. " + String.format("%.2f", total));

            // 3. Crear el objeto Cotizacion con TODOS sus datos
            Cotizacion objCot = new Cotizacion();
            objCot.setIdCotizacion(idCot);
            objCot.setIdCliente(idCli);
            objCot.setSubtotal(subtotal);
            objCot.setIgv(igv);
            objCot.setTotal(total);

            // 4. Instanciar el DAO y registrar
            boolean exito = cotizacionDAO.insertar(objCot);

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Cotización registrada con éxito en la BD!");
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar la cotización en la BD. Verifique si el ID ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa valores numéricos válidos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtIdCotizacion.setText("");
        txtIdCliente.setText("");
        txtPrecioUnitario.setText("");
        txtCantidad.setText("");
        lblSubtotal.setText("Subtotal: S/. 0.00");
        lblIgv.setText("IGV (18%): S/. 0.00");
        lblTotal.setText("Total: S/. 0.00");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CotizacionView().setVisible(true);
        });
    }
}