package com.maquimotor.vista;

import com.maquimotor.dao.EquipoDAO;
import com.maquimotor.modelo.Equipo;

import javax.swing.*;

public class EditarEquipoDialog extends JDialog {
    private JTextField txtCodigo, txtSerie, txtMarca, txtModelo, txtPrecio, txtStockDisp, txtStockMin;
    private JComboBox<String> cbAplicacion, cbEstado;
    private EquipoDAO equipoDAO;
    private Equipo equipoActual;
    private Runnable onSuccess;

    public EditarEquipoDialog(JFrame parent, EquipoDAO dao, Equipo equipo, Runnable onSuccess) {
        super(parent, "Actualizar Datos del Equipo / Estado (RF-03)", true);
        this.equipoDAO = dao;
        this.equipoActual = equipo;
        this.onSuccess = onSuccess;

        setSize(480, 460);
        setLocationRelativeTo(parent);
        setLayout(null);

        int y = 20;
        txtCodigo = addField("Código:", equipo.getCodigo(), y); y += 35;
        txtSerie = addField("N° Serie:", equipo.getNumeroSerie(), y); y += 35;
        txtMarca = addField("Marca:", equipo.getMarca(), y); y += 35;
        txtModelo = addField("Modelo:", equipo.getModelo(), y); y += 35;

        JLabel lApl = new JLabel("Aplicación:"); lApl.setBounds(30, y, 120, 25); add(lApl);
        cbAplicacion = new JComboBox<>(new String[]{"Minería", "Agricultura"});
        cbAplicacion.setSelectedItem(equipo.getAplicacion());
        cbAplicacion.setBounds(160, y, 260, 25); add(cbAplicacion);
        y += 35;

        txtPrecio = addField("Precio Base (S/.):", String.valueOf(equipo.getPrecioBase()), y); y += 35;
        txtStockDisp = addField("Stock Disponible:", String.valueOf(equipo.getStockDisponible()), y); y += 35;
        txtStockMin = addField("Stock Mínimo:", String.valueOf(equipo.getStockMinimo()), y); y += 35;

        JLabel lEst = new JLabel("Estado Físico/Op.:"); lEst.setBounds(30, y, 120, 25); add(lEst);
        cbEstado = new JComboBox<>(new String[]{"Disponible", "Reservado", "En Mantenimiento", "Vendido"});
        cbEstado.setSelectedItem(equipo.getEstado());
        cbEstado.setBounds(160, y, 260, 25); add(cbEstado);
        y += 45;

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBounds(140, y, 200, 35);
        btnGuardar.addActionListener(e -> actualizar());
        add(btnGuardar);
    }

    private JTextField addField(String label, String valorInicial, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(30, y, 120, 25);
        add(l);
        JTextField tf = new JTextField(valorInicial);
        tf.setBounds(160, y, 260, 25);
        add(tf);
        return tf;
    }

    private void actualizar() {
        try {
            equipoActual.setCodigo(txtCodigo.getText().trim());
            equipoActual.setNumeroSerie(txtSerie.getText().trim());
            equipoActual.setMarca(txtMarca.getText().trim());
            equipoActual.setModelo(txtModelo.getText().trim());
            equipoActual.setAplicacion((String) cbAplicacion.getSelectedItem());
            equipoActual.setPrecioBase(Double.parseDouble(txtPrecio.getText().trim()));
            equipoActual.setStockDisponible(Integer.parseInt(txtStockDisp.getText().trim()));
            equipoActual.setStockMinimo(Integer.parseInt(txtStockMin.getText().trim()));
            equipoActual.setEstado((String) cbEstado.getSelectedItem());

            if (equipoDAO.actualizar(equipoActual)) {
                JOptionPane.showMessageDialog(this, "¡Equipo actualizado exitosamente!");
                if (onSuccess != null) onSuccess.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el equipo en la BD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique los valores numéricos.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}