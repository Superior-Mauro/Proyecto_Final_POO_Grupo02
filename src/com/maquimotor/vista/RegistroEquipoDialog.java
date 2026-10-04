package com.maquimotor.vista;

import com.maquimotor.dao.EquipoDAO;
import com.maquimotor.modelo.Equipo;
import com.maquimotor.modelo.FichaTecnica;

import javax.swing.*;

public class RegistroEquipoDialog extends JDialog {
    private JTextField txtId, txtCodigo, txtSerie, txtMarca, txtModelo, txtPrecio, txtStockDisp, txtStockMin, txtHP, txtCilindrada, txtCompatibilidad;
    private JComboBox<String> cbAplicacion, cbCombustible, cbEstado;
    private EquipoDAO equipoDAO;
    private Runnable onSuccess;

    public RegistroEquipoDialog(JFrame parent, EquipoDAO dao, Runnable onSuccess) {
        super(parent, "Registrar Maquinaria / Motor Técnico", true);
        this.equipoDAO = dao;
        this.onSuccess = onSuccess;

        setSize(520, 570);
        setLocationRelativeTo(parent);
        setLayout(null);

        int y = 15;
        txtId = addField("ID Equipo:", y); y += 35;
        txtCodigo = addField("Código:", y); y += 35;
        txtSerie = addField("N° Serie:", y); y += 35;
        txtMarca = addField("Marca:", y); y += 35;
        txtModelo = addField("Modelo:", y); y += 35;

        JLabel lApl = new JLabel("Aplicación:"); lApl.setBounds(30, y, 130, 25); add(lApl);
        cbAplicacion = new JComboBox<>(new String[]{"Minería", "Agricultura"});
        cbAplicacion.setBounds(170, y, 290, 25); add(cbAplicacion);
        y += 35;

        txtPrecio = addField("Precio Base (S/.):", y); y += 35;
        txtStockDisp = addField("Stock Inicial:", y); y += 35;
        txtStockMin = addField("Stock Mínimo (Alerta):", y); y += 35;

        JLabel lEst = new JLabel("Estado Físico/Op.:"); lEst.setBounds(30, y, 130, 25); add(lEst);
        cbEstado = new JComboBox<>(new String[]{"Disponible", "Reservado", "Mantenimiento"});
        cbEstado.setBounds(170, y, 290, 25); add(cbEstado);
        y += 35;

        txtHP = addField("Potencia (HP):", y); y += 35;
        txtCilindrada = addField("Cilindrada (L):", y); y += 35;

        JLabel lComb = new JLabel("Combustible:"); lComb.setBounds(30, y, 130, 25); add(lComb);
        cbCombustible = new JComboBox<>(new String[]{"Diésel", "Gasolina", "Eléctrico"});
        cbCombustible.setBounds(170, y, 290, 25); add(cbCombustible);
        y += 35;

        txtCompatibilidad = addField("Compatibilidad:", y); y += 45;

        JButton btnGuardar = new JButton("Guardar en Catálogo");
        btnGuardar.setBounds(160, y, 200, 35);
        btnGuardar.addActionListener(e -> guardarEquipo());
        add(btnGuardar);
    }

    private JTextField addField(String label, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(30, y, 130, 25);
        add(l);
        JTextField tf = new JTextField();
        tf.setBounds(170, y, 290, 25);
        add(tf);
        return tf;
    }

    private void guardarEquipo() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            String cod = txtCodigo.getText().trim();
            String serie = txtSerie.getText().trim();
            String marca = txtMarca.getText().trim();
            String modelo = txtModelo.getText().trim();
            String apl = (String) cbAplicacion.getSelectedItem();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStockDisp.getText().trim());
            int stockMin = Integer.parseInt(txtStockMin.getText().trim());
            String estado = (String) cbEstado.getSelectedItem();

            double hp = Double.parseDouble(txtHP.getText().trim());
            double cil = Double.parseDouble(txtCilindrada.getText().trim());
            String comb = (String) cbCombustible.getSelectedItem();
            String comp = txtCompatibilidad.getText().trim();

            if (precio <= 0 || hp <= 0 || stock < 0) {
                JOptionPane.showMessageDialog(this, "El precio y los HP deben ser mayores a 0.", "Error Numérico", JOptionPane.WARNING_MESSAGE);
                return;
            }

            FichaTecnica ficha = new FichaTecnica(hp, cil, comb, comp);
            Equipo eq = new Equipo(id, cod, serie, marca, modelo, apl, precio, stock, stockMin, estado, ficha);

            if (equipoDAO.insertar(eq)) {
                JOptionPane.showMessageDialog(this, "¡Equipo registrado exitosamente en el inventario!");
                if (onSuccess != null) onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar. Verifique que el ID, Código y N° Serie sean únicos.", "Error de Inserción", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor complete los campos numéricos con valores válidos.", "Dato Inválido", JOptionPane.WARNING_MESSAGE);
        }
    }
}